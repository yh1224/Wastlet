package net.assemble.android.mywallet

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.lazy
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import net.assemble.android.common.BaseApplication
import net.assemble.android.common.preferences.Preferences
import net.assemble.android.mywallet.di.applicationModule
import timber.log.Timber

class MyApplication : BaseApplication() {
    override val kodein: Kodein by Kodein.lazy {
        import(applicationModule(this@MyApplication))
    }

    override fun onCreate() {
        super.onCreate()

        // Enable Timber (for debug build only)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Enable Crashlytics (for release build only)
        val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        // Initialize AndroidThreeTen
        AndroidThreeTen.init(this)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.getInstance().firestoreSettings =
                FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()

        // Initialize Preferences
        Preferences.init(applicationContext)

        // Initialize AdMob
        MobileAds.initialize(this, ADMOB_APP_ID)
    }

    companion object {
        private const val ADMOB_APP_ID = "ca-app-pub-6511207727081425~9483318604"
    }
}
