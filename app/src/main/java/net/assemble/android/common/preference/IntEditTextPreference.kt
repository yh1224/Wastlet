@file:Suppress("unused")

package net.assemble.android.common.preference

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet

class IntEditTextPreference : EditTextPreference {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun getPersistedString(defaultReturnValue: String?): String? {
        return getPersistedInt(-1).toString()
    }

    override fun persistString(value: String): Boolean {
        try {
            persistInt(value.toInt())
        } catch (e: NumberFormatException) {
        }
        return true
    }
}
