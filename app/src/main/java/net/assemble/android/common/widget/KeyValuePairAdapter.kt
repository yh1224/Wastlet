package net.assemble.android.common.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * @param context
 * @param resourceId
 * @param list 選択肢
 */
class KeyValuePairAdapter(context: Context, resourceId: Int, list: Array<Pair<String, String>>)
    : ArrayAdapter<Pair<String, String>>(context, resourceId, list) {
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getView(pos, convertView, parent) as TextView
        textView.text = getItem(pos)!!.second
        return textView
    }

    override fun getDropDownView(pos: Int, convertView: View?, parent: ViewGroup): View {
        val textView = super.getDropDownView(pos, convertView, parent) as TextView
        textView.text = getItem(pos)!!.second
        return textView
    }
}
