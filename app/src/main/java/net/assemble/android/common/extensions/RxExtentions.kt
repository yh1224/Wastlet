@file:Suppress("unused")

package net.assemble.android.common.extensions

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/*
 * disposables.add() の代わりに disposables += と書けるようにする
 */
operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

/*
 * 処理中は指定した View を表示
 */
fun <T> Observable<T>.withView(view: View): Observable<T> =
        Observable.using({
            view.visibility = View.VISIBLE
        }, { this }, {
            view.visibility = View.GONE
        })

fun <T> Single<T>.withView(view: View): Single<T> =
        Single.using({
            view.visibility = View.VISIBLE
        }, { this }, {
            view.visibility = View.GONE
        })

fun Completable.withView(view: View): Completable =
        Completable.using({
            view.visibility = View.VISIBLE
        }, { this }, {
            view.visibility = View.GONE
        })

fun <T> Single<T>.with(swipeRefreshLayout: SwipeRefreshLayout): Single<T> =
        Single.using({
            swipeRefreshLayout.isRefreshing = true
        }, { this }, {
            swipeRefreshLayout.isRefreshing = false
        })

fun <T> Single<T>.withViewDisable(view: View): Single<T> =
        Single.using({
            view.isEnabled = false
        }, { this }, {
            view.isEnabled = true
        })

fun Completable.withViewDisable(view: View): Completable =
        Completable.using({
            view.isEnabled = false
        }, { this }, {
            view.isEnabled = true
        })
