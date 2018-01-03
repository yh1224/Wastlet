package net.assemble.android.common.util

/**
 * Scala-like Option usable for RxJava
 *
 * RxJava では null を通知できないため、Scala ライクな Option クラスを用意
 */
sealed class Option<out T>(private val value: T?) {
    fun filter(f: (T) -> Boolean): Option<T> =
            if (value != null && f(value)) this else None

    fun <V> flatMap(f: (T) -> Option<V>): Option<V> =
            if (value != null) f(value) else None

    fun forEach(f: (T) -> Unit) {
        if (value != null) f(value)
    }

    fun get() = value!!

    fun <T> getOrElse(default: T) = value ?: default

    fun <T> getOrElse(f: () -> T) = value ?: f()

    fun getOrNull() = value

    fun isDefined() = value != null

    fun isEmpty() = value == null

    fun <V> map(f: (T) -> V): Option<V> =
            if (value != null) Some(f(value)) else None

    companion object {
        fun <T> apply(value: T?) = Some(value)
    }
}

/** Option that has some value */
class Some<out T>(value: T) : Option<T>(value)

/** Option that has none value */
object None : Option<Nothing>(null)
