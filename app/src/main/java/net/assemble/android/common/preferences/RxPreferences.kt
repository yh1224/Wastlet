package net.assemble.android.common.preferences

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposables
import io.reactivex.observables.ConnectableObservable
import kotlin.reflect.KProperty

abstract class RxPreferences : Preferences() {
    /**
     * Preferences が変更するたびに取得
     */
    val changes: Observable<out Preferences> by lazy {
        ConnectableObservable.create(ObservableOnSubscribe<Preferences> { emitter ->
            val listener = object : SharedPrefsListener {
                override fun onSharedPrefChanged(property: KProperty<*>) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(this@RxPreferences)
                    }
                }
            }

            // リスナーを登録
            addListener(listener)

            // 終了処理
            emitter.setDisposable(Disposables.fromAction {
                removeListener(listener)
            })
        })
    }
}
