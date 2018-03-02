package net.assemble.android.mywallet.fragment

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.instance
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.assemble.android.common.extensions.with
import net.assemble.android.common.filter.CurrencyFormatInputFilter
import net.assemble.android.common.fragment.BaseFragment
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.databinding.FragmentItemListBinding
import net.assemble.android.mywallet.repository.ItemRepositoryInterface
import net.assemble.android.mywallet.widget.WalletItemAdapter

class ItemListFragment : BaseFragment() {
    // Instances injected by Kodein
    private val itemRepository: ItemRepositoryInterface by instance()
    private val currencyFormatInputFilter: CurrencyFormatInputFilter by instance()
    private val bus: RxBus by instance()

    // Bindings
    private lateinit var binding: FragmentItemListBinding

    /** Disposable container for RxJava */
    private lateinit var disposables: CompositeDisposable

    /** 表示年 */
    private var year: Int = 0

    /** 表示月 */
    private var month: Int = 0

    /** 追加ボタンクリックイベント */
    class OnAddItemClickedEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        year = arguments!!.getInt(ARG_YEAR)
        month = arguments!!.getInt(ARG_MONTH)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false)

        // レイアウト内の RecyclerView を取得
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(activity).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager(activity).orientation))
        }

        with(binding.swipeRefresh) {
            setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
            setOnRefreshListener { refresh() }
        }

        // 追加ボタン
        binding.add.clicks().subscribe {
            bus.post(OnAddItemClickedEvent())
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        disposables = CompositeDisposable()

        refresh()
    }

    /**
     * 一覧を更新
     */
    private fun refresh() {
        // 一覧を RecyclerView に適用
        itemRepository.getMonthly(year, month)
                .with(binding.swipeRefresh)
                .subscribe { diaries ->
                    val totalFee = diaries.map { it.fee }.sum()
                    binding.totalFee.text = getString(R.string.total_fee,
                            currencyFormatInputFilter.formatWithSymbol(totalFee))
                    binding.recyclerView.adapter = WalletItemAdapter(diaries, currencyFormatInputFilter, bus)
                }
                .addTo(disposables)
    }

    override fun onPause() {
        super.onPause()

        disposables.dispose()
    }

    companion object {
        private const val ARG_YEAR = "year"
        private const val ARG_MONTH = "month"

        /**
         * @param year 年(4桁)
         * @param month 月(1～12)
         */
        fun newInstance(year: Int, month: Int) =
                ItemListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_YEAR, year)
                        putInt(ARG_MONTH, month)
                    }
                }
    }
}
