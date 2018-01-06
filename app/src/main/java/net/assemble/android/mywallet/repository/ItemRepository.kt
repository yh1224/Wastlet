package net.assemble.android.mywallet.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Single
import net.assemble.android.common.extensions.toCompletable
import net.assemble.android.common.extensions.toSingle
import net.assemble.android.common.util.None
import net.assemble.android.common.util.Option
import net.assemble.android.common.util.RxBus
import net.assemble.android.common.util.Some
import net.assemble.android.mywallet.entity.WalletItem
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * 購読ユーザに関するDB処理クラス
 */
class ItemRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        private val bus: RxBus
) : AppRepository(firebaseAuth, firebaseFirestore), ItemRepositoryInterface {
    override fun save(item: WalletItem): Completable =
            if (item.id != null) {
                itemsRef().document(item.id!!).set(item).toCompletable()
                        .doOnComplete {
                            bus.post(WalletItem.Event.OnUpdated(item))
                        }
            } else {
                itemsRef().add(item).toCompletable()
                        .doOnComplete {
                            bus.post(WalletItem.Event.OnAdded(item))
                        }
            }

    override fun getMonthly(year: Int, month: Int): Single<List<WalletItem>> {
        val start = LocalDateTime.of(year, month, 1, 0, 0, 0, 0).atZone(ZoneId.systemDefault())
        val end = start.plusMonths(1)
        Log.d("ItemRepo", "start=$start(${start.toInstant().toEpochMilli()}), end=$end(${end.toInstant().toEpochMilli()})")
        return itemsRef()
                .whereGreaterThanOrEqualTo(WalletItem.KEY_DATE, start.toInstant().toEpochMilli())
                .whereLessThan(WalletItem.KEY_DATE, end.toInstant().toEpochMilli())
                .orderBy(WalletItem.KEY_DATE, Query.Direction.ASCENDING)
                .orderBy(WalletItem.KEY_TIMESTAMP, Query.Direction.ASCENDING)
                .get()
                .toSingle()
                .map { querySnapshot ->
                    querySnapshot.map { documentSnapshot ->
                        documentSnapshot.toObject(WalletItem::class.java).apply {
                            id = documentSnapshot.id
                        }
                    }
                }
    }

    override fun get(id: String): Single<WalletItem> =
            itemsRef().document(id).get().toSingle()
                    .map { documentSnapshot ->
                        documentSnapshot.toObject(WalletItem::class.java)
                    }

    override fun getFirst(): Single<Option<WalletItem>> =
            itemsRef()
                    .orderBy(WalletItem.KEY_DATE, Query.Direction.ASCENDING)
                    .orderBy(WalletItem.KEY_TIMESTAMP, Query.Direction.ASCENDING)
                    .limit(1).get().toSingle()
                    .map { documentSnapshot ->
                        if (documentSnapshot.isEmpty) {
                            None
                        } else {
                            Some(documentSnapshot.first().toObject(WalletItem::class.java))
                        }
                    }

    override fun delete(id: String): Completable =
            itemsRef().document(id).delete().toCompletable()
                    .doOnComplete {
                        bus.post(WalletItem.Event.OnDeleted(id))
                    }
}
