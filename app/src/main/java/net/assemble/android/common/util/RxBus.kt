package net.assemble.android.common.util

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

@Suppress("unused")
/**
 * Implementation of Event Bus by RxJava
 *
 * ref)
 * https://blog.kaush.co/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/
 * https://qiita.com/yyaammaa/items/57d8baa1e80346e67e47
 */
object RxBus {
    private val mBus = PublishSubject.create<Any>().toSerialized()

    /**
     * Publish message
     */
    fun post(o: Any) {
        mBus.onNext(o)
    }

    /**
     * Get observable
     */
    fun toObservable(): Observable<Any> = mBus

    /**
     * Check if observers exists
     */
    fun hasObservers(): Boolean = mBus.hasObservers()
}
