package money.vivid.elmslie.core.store.dsl

@DslMarker internal annotation class OperationsBuilderDsl

@OperationsBuilderDsl
public class OperationsBuilder<T : Any> {

  private val list = mutableListOf<T>()

  public operator fun T?.unaryPlus() {
    this?.let(list::add)
  }

  internal fun build() = list
}
