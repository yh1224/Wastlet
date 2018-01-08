package net.assemble.android.mywallet.activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.github.salomonbrys.kodein.instance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.common.extensions.plusAssign
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.fragment.AppPreferenceFragment

/**
 * 設定
 */
class AppPreferenceActivity : BaseActivity() {
    // Instances injected by Kodein
    private val bus: RxBus by instance()

    /** Disposable container for RxJava */
    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.preference_activity)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)!!
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.settings)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
                .replace(R.id.content, AppPreferenceFragment.newInstance())
                .commit()

        disposables += bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { event ->
                    when (event) {
                        is AppPreferenceFragment.OnFinished -> {
                            finish()
                        }
                    }
                }
    }

    override fun onDestroy() {
        super.onDestroy()

        disposables.dispose()
    }

    companion object {
        @Suppress("unused")
        private val TAG = AppPreferenceActivity::class.java.simpleName
    }
}
