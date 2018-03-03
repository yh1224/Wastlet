package net.assemble.android.mywallet.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseAuth
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.preferences.AppPreferences

/**
 * 設定フラグメント
 */
class AppPreferenceFragment : BasePreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    // Instances injected by Kodein
    private val appPreferences: AppPreferences by instance()
    private val firebaseAuth: FirebaseAuth by instance()
    private val bus: RxBus by instance()

    /** 終了イベント */
    class OnFinished

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = AppPreferences::class.java.simpleName
        addPreferencesFromResource(R.xml.app_preferences)

        val prefs = activity!!.getSharedPreferences(AppPreferences::class.java.simpleName, Context.MODE_PRIVATE)
        prefs.registerOnSharedPreferenceChangeListener(this)

        initView()
    }

    private fun initView() {
        val logoutPreference = findPreference("logout")
        logoutPreference.summary = firebaseAuth.currentUser?.displayName
        logoutPreference.setOnPreferenceClickListener {
            firebaseAuth.signOut()
            finish()
            true
        }

        //val budgetPreference = findPreference("budget")
        //budgetPreference.summary = if (appPreferences.budget > 0) {
        //    NumberFormat.getCurrencyInstance().formatWithSymbol(appPreferences.budget)
        //} else {
        //    ""
        //}
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        initView()
    }

    /**
     * 設定画面終了
     */
    private fun finish() {
        bus.post(AppPreferenceFragment.OnFinished())
    }

    companion object {
        fun newInstance(): AppPreferenceFragment = AppPreferenceFragment()
    }
}
