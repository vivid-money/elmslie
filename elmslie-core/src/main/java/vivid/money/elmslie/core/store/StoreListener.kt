package vivid.money.elmslie.core.store

interface StoreListener<Event : Any, State : Any, Effect : Any, Command : Any> {

    fun onBeforeEvent(key: String, event: Event, currentState: State) {}
    fun onAfterEvent(key: String, newState: State, oldState: State, eventCause: Event) {}
    fun onEffect(key: String, effect: Effect, state: State) {}
    fun onCommand(key: String, command: Command, state: State) {}

    fun onReducerError(key: String, throwable: Throwable, event: Event) {}
    fun onActorError(key: String, throwable: Throwable, command: Command) {}
}
