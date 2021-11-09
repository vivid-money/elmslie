package vivid.money.elmslie.core.util

/**
 * Use this wrapper in case when expected not null value but null value can be present
 */
data class Option<T : Any>(val value: T?)
