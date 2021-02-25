@file:JvmName("FastLazy")

package vivid.money.elmslie.android.util

/**
 * Lazy initialization without synchronization
 */
internal fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }
