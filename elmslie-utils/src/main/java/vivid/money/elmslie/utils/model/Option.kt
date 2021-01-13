package vivid.money.elmslie.utils.model

/**
 * Use this wrapper in case when expected not null value but null value can be present
 */
data class Option<T : Any>(
    val value: T?
) {

    val isEmpty = value == null
    val hasValue = !isEmpty

    companion object {

        fun <T : Any> none() = Option<T>(null)
    }
}