@file:Suppress("unused")

package net.assemble.android.common.extensions

import com.google.android.gms.tasks.Task
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/*
 * Task of GMS (Google Mobile Services) to RxJava2
 */
fun <TResult> Task<TResult>.toObservable(): Observable<TResult> =
        Observable.create { emitter ->
            addOnSuccessListener {
                if (!emitter.isDisposed) {
                    emitter.onNext(result)
                    emitter.onComplete()
                }
            }
            addOnFailureListener { e ->
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }

fun <TResult> Task<TResult>.toSingle(): Single<TResult> =
        Single.create { emitter ->
            addOnSuccessListener {
                if (!emitter.isDisposed) emitter.onSuccess(result)
            }
            addOnFailureListener { e ->
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }

fun <TResult> Task<TResult>.toCompletable(): Completable =
        Completable.create { emitter ->
            addOnSuccessListener {
                if (!emitter.isDisposed) emitter.onComplete()
            }
            addOnFailureListener { e ->
                if (!emitter.isDisposed) emitter.onError(e)
            }
        }
