package vivid.money.elmslie.core.store

fun interface StateReducer<Event : Any, State : Any, Effect : Any, Command : Any> {

    fun reduce(event: Event, state: State): Result<State, Effect, Command>
}
