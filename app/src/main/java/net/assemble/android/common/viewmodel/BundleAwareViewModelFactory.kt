package net.assemble.android.common.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle

class BundleAwareViewModelFactory(
        private val bundle: Bundle?,
        private val provider: ViewModelProvider.Factory) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = provider.create(modelClass)
        if (viewModel is ParcelableViewModel) {
            bundle?.let { viewModel.readFrom(it) }
        }
        return viewModel
    }
}
