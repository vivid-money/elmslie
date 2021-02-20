package vivid.money.elmslie.core.store.dsl_reducer

class OperationsBuilder<T : Any> {

    private val list = mutableListOf<T>()

    operator fun invoke(vararg items: T?) {
        list.addAll(items.filterNotNull())
    }

    internal fun build() = list
}