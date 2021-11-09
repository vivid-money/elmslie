package vivid.money.elmslie.core.util

/**
 * Wraps a function with a thread safe filtering of subsequent equal values
 */
fun <T> ((T) -> Unit).distinctUntilChanged() = object : (T) -> Unit {

    @Volatile
    private var previousValue: T? = null

    override fun invoke(value: T) {
        val isUpdated = synchronized(this) {
            (previousValue != value).also { previousValue = value }
        }
        if (isUpdated) this@distinctUntilChanged(value)
    }
}
