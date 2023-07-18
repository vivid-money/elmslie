package vivid.money.elmslie.core.store

interface StoreListener<Event : Any, State : Any, Effect : Any, Command : Any> {

    fun onEvent(key: String, event: Event) {}
    fun onEffect(key: String, effect: Effect) {}
    fun onCommand(key: String, command: Command) {}
    fun onStateChanged(key: String, newState: State, oldState: State, eventCause: Event) {}

    fun onReducerError(key: String, throwable: Throwable, event: Event) {}
    fun onCommandError(key: String, throwable: Throwable, command: Command) {}
}
