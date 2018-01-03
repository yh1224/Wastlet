package net.assemble.android.mywallet.preferences

import net.assemble.android.common.preferences.RxPreferences

class AppPersist : RxPreferences() {
    var userTokenId by stringPref()
}
