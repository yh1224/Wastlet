package net.assemble.android.mywallet.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.github.salomonbrys.kodein.instance
import net.assemble.android.common.fragment.BasePreferenceFragment
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.preferences.AppPreferences
import java.text.NumberFormat

/**
 * 設定フラグメント
 */
class AppPreferenceFragment : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    // Instances injected by Kodein
    private val appPreferences: AppPreferences by instance()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppPreferences::class.java.simpleName
        addPreferencesFromResource(R.xml.app_preferences)

        val prefs = activity!!.getSharedPreferences(AppPreferences::class.java.simpleName, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(this)

        initView()
    }

    private fun initView() {
        val budgetPreference = findPreference("budget")
        budgetPreference.summary = if (appPreferences.budget > 0) {
            NumberFormat.getCurrencyInstance().format(appPreferences.budget)
        } else {
            ""
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        initView()
    }

    companion object {
        @Suppress("unused")
        private val TAG = AppPreferenceFragment::class.java.simpleName

        fun newInstance(): AppPreferenceFragment = AppPreferenceFragment()
    }
}
