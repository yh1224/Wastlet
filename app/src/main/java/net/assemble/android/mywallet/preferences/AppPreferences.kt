package net.assemble.android.mywallet.preferences

import android.content.Context
import net.assemble.android.common.preferences.RxPreferences

class AppPreferences(context: Context) : RxPreferences(context) {
    var budget by intPref()
}
