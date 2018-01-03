package net.assemble.android.mywallet.repository

import io.reactivex.Completable
import io.reactivex.Single
import net.assemble.android.common.util.Option
import net.assemble.android.mywallet.entity.WalletItem

interface ItemRepositoryInterface {
    /**
     * アイテムを保存
     *
     * @param item アイテム
     */
    fun save(item: WalletItem): Completable

    /**
     * 指定月のアイテムを取得
     *
     * @param year 年(4桁)
     * @param month 月(1～12)
     */
    fun getMonthly(year: Int, month: Int): Single<List<WalletItem>>

    /**
     * アイテムを取得
     *
     * @param id アイテムID
     */
    fun get(id: String): Single<WalletItem>

    /**
     * 先頭のアイテムを取得
     */
    fun getFirst(): Single<Option<WalletItem>>

    /**
     * アイテムを削除
     *
     * @param id アイテムID
     */
    fun delete(id: String): Completable
}
