package net.assemble.android.mywallet.fragment

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.github.salomonbrys.kodein.instance
import com.jakewharton.rxbinding2.view.clicks
import net.assemble.android.common.extensions.withViewDisable
import net.assemble.android.common.fragment.BaseFragment
import net.assemble.android.common.fragment.DatePickerDialogFragment
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.databinding.ItemEditFragmentBinding
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
    private val bus: RxBus by instance()

    // Bindings
    private lateinit var binding: ItemEditFragmentBinding
    private lateinit var form: ItemEditForm

    data class ItemEditForm(
            val id: String?,
            val fee: ObservableField<String> = ObservableField(""),
            val note: ObservableField<String> = ObservableField(""),
            val date: ObservableField<String> = ObservableField("")
    ) : Serializable

    /** 確認メッセージ */
    private var confirmSnackbar: Snackbar? = null

    /** 終了イベント */
    class OnFinished

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        form = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(ARG_ITEM_INFO) as ItemEditForm
        } else {
            val itemInfo = (arguments.getSerializable(ARG_ITEM_INFO) as WalletItem).copy()
            ItemEditForm(itemInfo.id).apply {
                fee.set(itemInfo.fee.toString())
                note.set(itemInfo.note)
                date.set(SimpleDateFormat("yyyy/MM/dd", Locale.US).format(itemInfo.date))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_edit_fragment, container, false)
        binding.form = form

        // 金額にフォーカス、全選択、IME ON
        binding.executePendingBindings()
        binding.itemFeeEdit.requestFocus()
        binding.itemFeeEdit.selectAll()
        inputMethodManager.hideSoftInputFromWindow(binding.itemFeeEdit.windowToken, 0)
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)

        binding.itemDate.clicks().subscribe {
            val d = form.date.get().split("/")
            DatePickerDialogFragment.newInstance(d[0].toInt(), d[1].toInt(), d[2].toInt()).show(childFragmentManager, DatePickerDialogFragment::class.java.simpleName)
        }

        binding.ok.clicks().subscribe {
            onOkClicked()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (form.id != null) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (form.id != null) {
            inflater.inflate(R.menu.menu_item_edit, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete -> {
                confirmSnackbar = Snackbar.make(view!!, R.string.delete_confirm, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.delete, {
                            itemRepository.delete(form.id!!)
                                    .withViewDisable(binding.ok)
                                    .subscribe {
                                        finish()
                                    }
                        })
                confirmSnackbar?.show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDatePicked(year: Int, month: Int, dayOfMonth: Int) {
        form.date.set("%04d/%02d/%02d".format(year, month, dayOfMonth))
    }

    private fun onOkClicked() {
        val itemInfo = WalletItem().apply {
            id = form.id
            fee = form.fee.get().toInt() // TODO: validate
            note = form.note.get()
            date = SimpleDateFormat("yyyy/MM/dd", Locale.US).parse(form.date.get()).time
        }
        itemRepository.save(itemInfo)
                .withViewDisable(binding.ok)
                .subscribe {
                    finish()
                }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(ARG_ITEM_INFO, form)
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
        @Suppress("unused")
        private val TAG = ItemEditFragment::class.java.simpleName

        private const val ARG_ITEM_INFO = "ITEM_INFO"

        fun newInstance(itemInfo: WalletItem = WalletItem()): ItemEditFragment =
                ItemEditFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_ITEM_INFO, itemInfo)
                    }
                }
    }
}
