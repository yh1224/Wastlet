package net.assemble.android.common.filter

import android.text.InputFilter
import android.text.Spanned
import java.util.*
import java.util.regex.Pattern

open class CurrencyFormat(private val locale: Locale) : InputFilter {
    private val currency = Currency.getInstance(locale)!!
    private val symbol = currency.symbol
    protected val fractionDigits = currency.defaultFractionDigits
    private val fractionPow = (1..fractionDigits).fold(1, { acc, _ -> acc * 10 })

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val pattern = Pattern.compile("(0|[1-9]+[0-9]*)" + if (fractionDigits > 0) "?(\\.[0-9]{0,$fractionDigits})?" else "")
        val result = (dest.subSequence(0, dstart).toString() + source.toString() + dest.subSequence(dend, dest.length))
        val matcher = pattern.matcher(result)
        return if (!matcher.matches()) dest.subSequence(dstart, dend) else null
    }

    /**
     * 金額(整数値)を金額(小数値)に変換
     *
     * @param fee 金額(整数値)
     */
    fun format(fee: Int) = ("%d" + if (fractionDigits > 0) ".%0${fractionDigits}d" else "")
            .format(fee / fractionPow, fee % fractionPow)

    /**
     * 金額(整数値)を金額文字列に変換
     *
     * @param fee 金額(整数値)
     */
    fun formatWithSymbol(fee: Int) = symbol + format(fee)

    /**
     * 金額(小数値)を金額(整数値)に変換
     *
     * @param feeStr 金額(整数値)
     */
    fun parse(feeStr: String) = feeStr.split(".").let {
        it[0].toInt() * fractionPow + if (it.count() > 1) it[1].toInt() else 0
    }
}
