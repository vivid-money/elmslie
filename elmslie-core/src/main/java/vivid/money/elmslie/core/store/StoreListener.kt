package vivid.money.elmslie.core.store

interface StoreListener<Event : Any, State : Any, Effect : Any, Command : Any> {

    fun onEvent(event: Event) {}
    fun onEffect(effect: Effect) {}
    fun onCommand(command: Command) {}
    fun onStateChanged(newState: State, oldState: State, eventCause: Event) {}

    fun onReducerError(throwable: Throwable, event: Event) {}
    fun onCommandError(throwable: Throwable, command: Command) {}
}
