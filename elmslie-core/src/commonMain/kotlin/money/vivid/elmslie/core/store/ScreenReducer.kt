package money.vivid.elmslie.core.store

import kotlin.reflect.KClass

abstract class ScreenReducer<
  Event : Any,
  Ui : Any,
  Internal : Any,
  State : Any,
  Effect : Any,
  Command : Any,
>(private val uiEventClass: KClass<Ui>, private val internalEventClass: KClass<Internal>) :
  StateReducer<Event, State, Effect, Command>() {

  protected abstract fun Result.ui(event: Ui): Any?

  protected abstract fun Result.internal(event: Internal): Any?

  override fun Result.reduce(event: Event) {
    @Suppress("UNCHECKED_CAST")
    when {
      uiEventClass.isInstance(event) -> ui(event as Ui)
      internalEventClass.isInstance(event) -> internal(event as Internal)
      else -> error("Event ${event::class} is neither UI nor Internal")
    }
  }
}
