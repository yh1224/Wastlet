package net.assemble.android.mywallet.widget

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.assemble.android.common.util.RxBus
import net.assemble.android.mywallet.R
import net.assemble.android.mywallet.entity.WalletItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class WalletItemAdapter(
        private val data: List<WalletItem>,
        private val bus: RxBus
) : RecyclerView.Adapter<WalletItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        val date = itemView.findViewById(R.id.itemDate) as TextView
        val note = itemView.findViewById(R.id.itemNote) as TextView
        val fee = itemView.findViewById(R.id.itemFee) as TextView
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
        holder.fee.text = NumberFormat.getCurrencyInstance().format(item.fee)
    }
}
