package money.vivid.elmslie.core.store

/** Reducer that doesn't change state, and doesn't emit commands or effects */
class NoOpReducer<Event : Any, State : Any, Effect : Any, Command : Any> :
  StateReducer<Event, State, Effect, Command>() {

  override fun Result.reduce(event: Event) = Unit
}
