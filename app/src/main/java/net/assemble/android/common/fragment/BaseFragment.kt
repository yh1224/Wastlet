package net.assemble.android.common.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.SupportFragmentInjector
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate

abstract class BaseFragment : Fragment()
        , SupportFragmentInjector, KodeinInjected
        , Pikkel by PikkelDelegate() {
    override val injector: KodeinInjector = KodeinInjector()

    //override fun provideOverridingModule() = Kodein.Module {
    //    import(fragmentModule(this@BaseFragment))
    //}

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeInjector()
        super.onCreate(savedInstanceState)
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
