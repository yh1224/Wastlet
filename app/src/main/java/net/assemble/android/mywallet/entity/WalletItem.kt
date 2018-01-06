package net.assemble.android.mywallet.entity

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*

data class WalletItem(
        /** key */
        @Exclude var id: String? = null,

        /** 保存日時 */
        var timestamp: Long = Calendar.getInstance().timeInMillis,

        /** 備考 */
        var note: String = "",

        /** 金額 */
        var fee: Int = 0,

        /** 日付 */
        var date: Long = Calendar.getInstance().timeInMillis
) : Serializable {
    sealed class Event {
        /** 追加イベント */
        data class OnAdded(val itemInfo: WalletItem) : Event()

        /** 変更イベント */
        data class OnUpdated(val itemInfo: WalletItem) : Event()

        /** 削除イベント */
        data class OnDeleted(val itemId: String) : Event()
    }

    companion object {
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_DATE = "date"
    }
}
