package net.assemble.android.common.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel

abstract class ParcelableViewModel : ViewModel() {
    abstract fun writeTo(bundle: Bundle)
    abstract fun readFrom(bundle: Bundle)
}
