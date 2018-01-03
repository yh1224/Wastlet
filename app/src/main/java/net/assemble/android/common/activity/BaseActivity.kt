package net.assemble.android.common.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.AppCompatActivityInjector
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate

abstract class BaseActivity : AppCompatActivity()
        , AppCompatActivityInjector, KodeinInjected
        , Pikkel by PikkelDelegate() {
    override val injector: KodeinInjector = KodeinInjector()

    //override fun provideOverridingModule() = Kodein.Module {
    //    import(activityModule(this@BaseActivity))
    //}

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeInjector()
        super.onCreate(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreInstanceState(savedInstanceState) // Saved states are restored here by Pikkel
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState) // Save states here by Pikkel
    }

    override fun onDestroy() {
        destroyInjector()
        super.onDestroy()
    }
}
