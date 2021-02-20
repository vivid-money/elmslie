package vivid.money.elmslie.core.store.dsl_reducer

class OperationsBuilder<T : Any> {

    private val list = mutableListOf<T>()

    operator fun plusAssign(item: T?) {
        item?.let(list::add)
    }

    operator fun plusAssign(items: Iterable<T>?) {
        items?.let(list::addAll)
    }

    operator fun invoke(vararg items: T) {
        list.addAll(items)
    }

    internal fun build() = list
}