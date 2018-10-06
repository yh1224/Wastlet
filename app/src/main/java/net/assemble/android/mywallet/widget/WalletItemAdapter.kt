package net.assemble.android.mywallet.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.assemble.android.common.filter.CurrencyFormatInputFilter
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.entity.WalletItem
import java.text.SimpleDateFormat
import java.util.*

class WalletItemAdapter(
        private val data: List<WalletItem>,
        private val currencyFormatInputFilter: CurrencyFormatInputFilter,
        private val bus: RxBus
) : RecyclerView.Adapter<WalletItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val date = itemView.findViewById<TextView>(R.id.itemDate)!!
        val note = itemView.findViewById<TextView>(R.id.itemNote)!!
        val fee = itemView.findViewById<TextView>(R.id.itemFee)!!
    }

    class OnItemClickEvent(val itemInfo: WalletItem)

    override fun getItemCount() = data.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_info, parent, false)
        val holder = ItemViewHolder(itemView)
        itemView.setOnClickListener {
            val item = data[holder.adapterPosition]
            bus.post(OnItemClickEvent(item))
        }
        return holder
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = data[position]
        holder.date.text = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(item.date)
        holder.note.text = item.note
        holder.fee.text = currencyFormatInputFilter.formatCurrency(item.fee)
    }
}
