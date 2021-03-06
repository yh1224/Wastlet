package net.assemble.android.mywallet.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.databinding.ActivityMainBinding
import net.assemble.android.mywallet.entity.WalletItem
import net.assemble.android.mywallet.fragment.ItemListFragment
import net.assemble.android.mywallet.repository.ItemRepositoryInterface
import net.assemble.android.mywallet.widget.WalletItemAdapter
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MainActivity : BaseActivity()
        , NavigationView.OnNavigationItemSelectedListener {
    // Instances injected by Kodein
    private val bus: RxBus by instance()
    private val firebaseAuth: FirebaseAuth by instance()
    private val itemRepository: ItemRepositoryInterface by instance()

    // Bindings
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerToggle: ActionBarDrawerToggle

    /** Disposable container for RxJava */
    private val disposables = CompositeDisposable()

    /** Firebase AuthStateListener */
    private val authStateListener = FirebaseAuth.AuthStateListener {
        if (firebaseAuth.currentUser == null) {
            initView()
        }
    }

    /**
     * ViewPager に月毎のリストを表示するフラグメントを渡すアダプタ
     */
    inner class MonthlyPagerAdapter(
            fragmentManager: FragmentManager,
            private val monthList: List<String>
    ) : FragmentStatePagerAdapter(fragmentManager) {
        override fun getItem(position: Int): ItemListFragment {
            val m = monthList[position].split("/")
            return ItemListFragment.newInstance(m[0].toInt(), m[1].toInt())
        }

        override fun getCount() = monthList.size
        override fun getPageTitle(position: Int) = monthList[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // ナビゲーションドロワーの生成
        drawerToggle = ActionBarDrawerToggle(this,
                binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        binding.drawerLayout.addDrawerListener(drawerToggle)
        binding.drawerNavigation.setNavigationItemSelectedListener(this)

        // Load an ad into the AdMob banner view.
        val adView = findViewById<AdView>(R.id.adView)!!
        val adRequest = AdRequest.Builder().setRequestAgent("android_studio:ad_template").build()
        adView.loadAd(adRequest)

        bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    when (event) {
                        is WalletItem.Event.OnAdded -> initView(event.itemInfo)
                        is WalletItem.Event.OnUpdated -> initView(event.itemInfo)
                        is WalletItem.Event.OnDeleted -> initView()
                        is ItemListFragment.OnAddItemClickedEvent -> startEditActivity()
                        is WalletItemAdapter.OnItemClickEvent -> startEditActivity(event.itemInfo)
                    }
                }
                .addTo(disposables)
    }

    /**
     * 表示を更新
     *
     * @param itemInfo 追加・変更したアイテム
     */
    private fun initView(itemInfo: WalletItem? = null) {
        val pager = findViewById<ViewPager>(R.id.pager)!!
        pager.adapter = null

        // 未ログイン時はログイン画面へ
        if (firebaseAuth.currentUser == null) {
            startActivityForResult(Intent(this, LoginActivity::class.java), REQUEST_LOGIN)
            return
        }

        itemRepository.getFirst().subscribe { itemOpt ->
            // 先頭から現在の月まで
            val monthList = mutableListOf<String>()
            val startMilli = itemOpt.map { it.date }.getOrNull()
                    ?: Calendar.getInstance().timeInMillis
            val startDate = Instant.ofEpochMilli(startMilli).atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1)
            val endDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
            var d = startDate
            while (d <= endDate) {
                monthList.add(d.format(DateTimeFormatter.ofPattern(MONTH_FORMAT)))
                d = d.plusMonths(1)
            }

            val adapter = MonthlyPagerAdapter(supportFragmentManager, monthList.toList())
            pager.adapter = adapter
            if (itemInfo != null) {
                // 追加・更新されたアイテムのページ
                pager.currentItem = monthList.indexOf(Instant.ofEpochMilli(itemInfo.date).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern(MONTH_FORMAT)))
            } else {
                pager.currentItem = monthList.count() - 1 // 最新
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        // Handle your other action bar items...
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_settings -> {
                startActivity(Intent(this, AppPreferenceActivity::class.java))
            }
            R.id.menu_item_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()

        firebaseAuth.addAuthStateListener(authStateListener)

        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                initView()
            } else {
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()

        firebaseAuth.removeAuthStateListener(authStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }

    /**
     * 編集画面を開く
     */
    private fun startEditActivity(itemInfo: WalletItem? = null) {
        startActivity(Intent(this, ItemEditActivity::class.java).apply {
            if (itemInfo != null) {
                putExtra(ItemEditActivity.EXTRA_ITEM_INFO, itemInfo)
            }
        })
    }

    companion object {
        private const val REQUEST_LOGIN = 1

        private const val MONTH_FORMAT = "yyyy/MM"
    }
}
