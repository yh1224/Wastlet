package net.assemble.android.mywallet.preferences

import net.assemble.android.common.preferences.RxPreferences

class AppPreferences : RxPreferences() {
    var budget by intPref()
}
