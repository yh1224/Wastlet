package net.assemble.android.common

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.github.salomonbrys.kodein.*

abstract class BaseApplication : MultiDexApplication(),
        KodeinAware, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    //override val kodein: Kodein by Kodein.lazy {
    //    import(applicationModule(this@BaseApplication))
    //}

    override fun onCreate() {
        inject(Kodein {
            extend(kodein)
            bind<Context>() with instance(applicationContext)
        })
        super.onCreate()
    }
}
