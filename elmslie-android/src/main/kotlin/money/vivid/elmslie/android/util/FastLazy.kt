package money.vivid.elmslie.android.util

/** Lazy initialization without synchronization */
internal fun <T> fastLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }
