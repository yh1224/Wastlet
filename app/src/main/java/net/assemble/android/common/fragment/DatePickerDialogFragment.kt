package net.assemble.android.common.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import java.util.*

class DatePickerDialogFragment : DialogFragment()
        , DatePickerDialog.OnDateSetListener {
    /** result listener */
    interface OnDatePickerResultListener {
        /**
         * 日付が選択された
         *
         * @param year 年(4桁)
         * @param month 月(1～12)
         * @param dayOfMonth 日(1～31)
         */
        fun onDatePicked(year: Int, month: Int, dayOfMonth: Int)
    }

    /** result listener object */
    private var listener: OnDatePickerResultListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = arguments!!.getInt(ARG_YEAR, c.get(Calendar.YEAR))
        val month = arguments!!.getInt(ARG_MONTH, c.get(Calendar.MONTH) + 1)
        val dayOfMonth = arguments!!.getInt(ARG_DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))

        return DatePickerDialog(activity, this, year, month - 1, dayOfMonth)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        val parent = parentFragment
        listener = when {
            parent is OnDatePickerResultListener -> parent
            context is OnDatePickerResultListener -> context
            else -> throw IllegalArgumentException()
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        listener?.onDatePicked(year, month + 1, dayOfMonth)
    }

    companion object {
        @Suppress("unused")
        private val TAG = DatePickerDialogFragment::class.java.simpleName

        private const val ARG_YEAR = "year"
        private const val ARG_MONTH = "month"
        private const val ARG_DAY_OF_MONTH = "dayOfMonth"

        /**
         * @param year 年(4桁)
         * @param month 月(1～12)
         * @param dayOfMonth 日(1～31)
         */
        fun newInstance(year: Int? = null, month: Int? = null, dayOfMonth: Int? = null): DatePickerDialogFragment =
                DatePickerDialogFragment().apply {
                    arguments = Bundle().apply {
                        if (year != null) putInt(ARG_YEAR, year)
                        if (month != null) putInt(ARG_MONTH, month)
                        if (dayOfMonth != null) putInt(ARG_DAY_OF_MONTH, dayOfMonth)
                    }
                }
    }
}
