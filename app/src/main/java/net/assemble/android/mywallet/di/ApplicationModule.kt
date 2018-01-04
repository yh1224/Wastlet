package net.assemble.android.mywallet.di

import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.helper.PackageInfoHelper
import net.assemble.android.mywallet.preferences.AppPreferences
import net.assemble.android.mywallet.repository.FirestoreRepository
import net.assemble.android.mywallet.repository.FirestoreRepositoryInterface
import net.assemble.android.mywallet.repository.ItemRepository
import net.assemble.android.mywallet.repository.ItemRepositoryInterface

fun applicationModule(application: Application) = Kodein.Module {
    bind<Application>() with instance(application)

    bind<InputMethodManager>() with singleton {
        application.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    bind<RxBus>() with instance(RxBus)

    bind<PackageInfoHelper>() with singleton {
        PackageInfoHelper(application.applicationContext)
    }

    bind<AppPreferences>() with singleton {
        AppPreferences()
    }

    bind<FirebaseAnalytics>() with singleton {
        FirebaseAnalytics.getInstance(application.applicationContext)
    }

    bind<FirebaseAuth>() with singleton {
        FirebaseAuth.getInstance()
    }

    bind<FirebaseFirestore>() with singleton {
        FirebaseFirestore.getInstance()
    }

    bind<FirestoreRepositoryInterface>() with singleton {
        FirestoreRepository(instance())
    }

    bind<ItemRepositoryInterface>() with singleton {
        ItemRepository(instance(), instance(), instance())
    }
}
