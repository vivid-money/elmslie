package vivid.money.elmslie.android.util

/**
 * Lazy initialization without synchronization
 */
fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }
