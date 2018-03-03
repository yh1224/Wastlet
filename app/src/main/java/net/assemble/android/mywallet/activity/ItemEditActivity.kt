package net.assemble.android.mywallet.activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.github.salomonbrys.kodein.instance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.entity.WalletItem
import net.assemble.android.mywallet.fragment.ItemEditFragment

class ItemEditActivity : BaseActivity() {
    // Instances injected by Kodein
    private val bus: RxBus by instance()

    /** Disposable container for RxJava */
    private lateinit var disposables: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_edit)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        disposables = CompositeDisposable()

        bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    when (event) {
                        is ItemEditFragment.OnFinished -> {
                            finish()
                        }
                    }
                }
                .addTo(disposables)

        if (supportFragmentManager.findFragmentByTag(ItemEditFragment::class.java.simpleName) == null) {
            val itemInfo = intent.getSerializableExtra(EXTRA_ITEM_INFO) as WalletItem?
            val itemEditFragment = if (itemInfo != null) {
                ItemEditFragment.newInstance(itemInfo) // 編集
            } else {
                ItemEditFragment.newInstance() // 新規
            }
            supportFragmentManager.beginTransaction()
                    .add(R.id.content, itemEditFragment, ItemEditFragment::class.java.simpleName)
                    .commit()
        }
    }

    companion object {
        const val EXTRA_ITEM_INFO = "itemInfo"
    }
}
