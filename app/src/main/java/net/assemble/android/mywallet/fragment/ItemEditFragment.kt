package net.assemble.android.mywallet.fragment

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.salomonbrys.kodein.instance
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.assemble.android.common.filter.CurrencyFormatInputFilter
import net.assemble.android.common.fragment.DatePickerDialogFragment
import net.assemble.android.common.util.RxBus
import net.assemble.android.common.viewmodel.BundleAwareViewModelFactory
import net.assemble.android.common.viewmodel.ParcelableViewModel
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.databinding.FragmentItemEditBinding
import net.assemble.android.mywallet.entity.WalletItem
import net.assemble.android.mywallet.repository.ItemRepositoryInterface
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class ItemEditFragment : BaseFragment()
        , DatePickerDialogFragment.OnDatePickerResultListener {
    // Instances injected by Kodein
    private val inputMethodManager: InputMethodManager by instance()
    private val itemRepository: ItemRepositoryInterface by instance()
    private val currencyFormatInputFilter: CurrencyFormatInputFilter by instance()
    private val bus: RxBus by instance()

    // Bindings
    private lateinit var binding: FragmentItemEditBinding
    private lateinit var viewModel: ItemEditViewModel

    /** Disposable container for RxJava */
    private val disposables = CompositeDisposable()

    class ItemEditViewModel : ParcelableViewModel(), Serializable {
        var id: String? = null
        val fee: ObservableField<String> = ObservableField("")
        val note: ObservableField<String> = ObservableField("")
        val date: ObservableField<String> = ObservableField("")

        override fun readFrom(bundle: Bundle) {
            val oldViewModel = bundle.getSerializable(ARG_VIEW_MODEL) as ItemEditViewModel
            id = oldViewModel.id
            fee.set(oldViewModel.fee.get())
            note.set(oldViewModel.note.get())
            date.set(oldViewModel.date.get())
        }

        override fun writeTo(bundle: Bundle) {
            bundle.putSerializable(ARG_VIEW_MODEL, this)
        }

        companion object {
            const val ARG_VIEW_MODEL = "fee"
        }
    }

    /** 確認メッセージ */
    private var confirmSnackbar: Snackbar? = null

    /** 終了イベント */
    class OnFinished

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create or restore viewModel
        viewModel = ViewModelProviders
                .of(activity!!, BundleAwareViewModelFactory(savedInstanceState, ViewModelProvider.NewInstanceFactory()))
                .get(ItemEditViewModel::class.java)

        // Set initial value
        if (savedInstanceState == null) {
            (arguments?.getSerializable(ARG_ITEM_INFO) as WalletItem?)?.let { itemInfo ->
                viewModel.apply {
                    id = itemInfo.id
                    fee.set(currencyFormatInputFilter.format(itemInfo.fee))
                    note.set(itemInfo.note)
                    date.set(SimpleDateFormat("yyyy/MM/dd", Locale.US).format(itemInfo.date))
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_edit, container, false)
        binding.viewModel = viewModel

        // 金額にフォーカス、全選択、IME ON
        binding.executePendingBindings()
        binding.itemFeeEdit.requestFocus()
        binding.itemFeeEdit.selectAll()
        inputMethodManager.hideSoftInputFromWindow(binding.itemFeeEdit.windowToken, 0)
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)

        binding.itemFeeEdit.filters = arrayOf(currencyFormatInputFilter)

        binding.itemDate.clicks().subscribe {
            val d = viewModel.date.get()!!.split("/")
            DatePickerDialogFragment.newInstance(d[0].toInt(), d[1].toInt(), d[2].toInt()).show(childFragmentManager, DatePickerDialogFragment::class.java.simpleName)
        }.addTo(disposables)

        binding.ok.clicks().subscribe {
            onOkClicked()
        }.addTo(disposables)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (viewModel.id != null) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.id != null) {
            inflater.inflate(R.menu.menu_item_edit, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete -> {
                confirmSnackbar = Snackbar.make(view!!, R.string.delete_confirm, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.delete, {
                            itemRepository.delete(viewModel.id!!)
                            Toast.makeText(activity, R.string.deleted, Toast.LENGTH_SHORT).show()
                            finish()
                        })
                confirmSnackbar?.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDatePicked(year: Int, month: Int, dayOfMonth: Int) {
        viewModel.date.set("%04d/%02d/%02d".format(year, month, dayOfMonth))
    }

    private fun onOkClicked() {
        // Validate fee
        val feeNum = try {
            currencyFormatInputFilter.parse(viewModel.fee.get()!!)
        } catch (e: NumberFormatException) {
            binding.itemFeeEdit.error = getString(R.string.error_fee_invalid)
            binding.itemFeeEdit.requestFocus()
            return
        }

        val itemInfo = ((arguments?.getSerializable(ARG_ITEM_INFO) as WalletItem?)
                ?: WalletItem()).apply {
            fee = feeNum
            note = viewModel.note.get()!!
            date = SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(viewModel.date.get()).time
        }
        itemRepository.save(itemInfo)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        viewModel.writeTo(outState)
    }

    override fun onPause() {
        super.onPause()

        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
        confirmSnackbar?.dismiss()
    }

    /**
     * 入力画面終了
     */
    private fun finish() {
        bus.post(OnFinished())
    }

    companion object {
        private const val ARG_ITEM_INFO = "ITEM_INFO"

        fun newInstance(itemInfo: WalletItem = WalletItem()): ItemEditFragment =
                ItemEditFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_ITEM_INFO, itemInfo)
                    }
                }
    }
}
