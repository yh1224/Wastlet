package net.assemble.android.common.fragment

import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.SupportFragmentInjector
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate

abstract class BaseDialogFragment : DialogFragment()
        , SupportFragmentInjector, KodeinInjected
        , Pikkel by PikkelDelegate() {
    override val injector: KodeinInjector = KodeinInjector()

    //override fun provideOverridingModule() = Kodein.Module {
    //    import(fragmentModule(this@BaseDialogFragment))
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