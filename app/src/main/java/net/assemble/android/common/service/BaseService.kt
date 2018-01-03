package net.assemble.android.common.service

import android.app.Service
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.ServiceInjector

abstract class BaseService : Service()
        , ServiceInjector, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    override fun onCreate() {
        initializeInjector()
        super.onCreate()
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }
}
