package net.assemble.android.mywallet.activity

import android.os.Bundle
import android.support.v7.widget.Toolbar
import net.assemble.android.common.activity.BaseActivity
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.fragment.AppPreferenceFragment

/**
 * 設定
 */
class AppPreferenceActivity : BaseActivity() {
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
    }

    companion object {
        @Suppress("unused")
        private val TAG = AppPreferenceActivity::class.java.simpleName
    }
}
