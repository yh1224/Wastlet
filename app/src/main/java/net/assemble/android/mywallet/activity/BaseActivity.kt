package net.assemble.android.mywallet.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector

abstract class BaseActivity : AppCompatActivity()
        , AppCompatActivityInjector, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    //override fun provideOverridingModule() = Kodein.Module {
    //    import(activityModule(this@BaseActivity))
    //}

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeInjector()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }
}
