package net.assemble.android.common.filter

import android.text.InputFilter
import android.text.Spanned
import java.util.*
import java.util.regex.Pattern

class CurrencyFormatInputFilter(locale: Locale) : CurrencyFormat(locale), InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        val pattern = Pattern.compile("(0|[1-9]+[0-9]*)?" + if (fractionDigits > 0) "?(\\.[0-9]{0,$fractionDigits})?" else "")
        val result = (dest.subSequence(0, dstart).toString() + source.toString() + dest.subSequence(dend, dest.length))
        val matcher = pattern.matcher(result)
        return if (!matcher.matches()) dest.subSequence(dstart, dend) else null
    }
}
