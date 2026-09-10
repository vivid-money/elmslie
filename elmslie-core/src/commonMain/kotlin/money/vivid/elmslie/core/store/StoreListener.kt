package money.vivid.elmslie.core.store

public interface StoreListener<Event : Any, State : Any, Effect : Any, Command : Any> {

  public fun onBeforeEvent(key: String, event: Event, currentState: State) {}

  public fun onAfterEvent(key: String, newState: State, oldState: State, eventCause: Event) {}

  public fun onEffect(key: String, effect: Effect, state: State) {}

  public fun onCommand(key: String, command: Command, state: State) {}

  public fun onReducerError(key: String, throwable: Throwable, event: Event) {}

  public fun onActorError(key: String, throwable: Throwable, command: Command) {}
}
