package net.assemble.android.mywallet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.SupportFragmentInjector

abstract class BaseFragment : Fragment()
        , SupportFragmentInjector, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    //override fun provideOverridingModule() = Kodein.Module {
    //    import(fragmentModule(this@BaseFragment))
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
