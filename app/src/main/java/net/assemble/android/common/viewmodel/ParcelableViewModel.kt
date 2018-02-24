package net.assemble.android.common.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Bundle

abstract class ParcelableViewModel : ViewModel() {
    abstract fun writeTo(bundle: Bundle)
    abstract fun readFrom(bundle: Bundle)
}
