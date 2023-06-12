package vivid.money.elmslie.core.store.dsl

@DslMarker
internal annotation class OperationsBuilderDsl

@OperationsBuilderDsl
class OperationsBuilder<T : Any> {

    private val list = mutableListOf<T>()

    operator fun T?.unaryPlus() {
        this?.let(list::add)
    }

    internal fun build() = list
}
