package vivid.money.elmslie.core.store.dsl_reducer

import vivid.money.elmslie.core.store.StateReducer

abstract class DslReducer<Event : Any, State : Any, Effect : Any, Command : Any> :
    StateReducer<Event, State, Effect, Command> {

    // Needed to type less code
    protected inner class Result(state: State) : ResultBuilder<State, Effect, Command>(state)

    protected abstract fun Result.reduce(event: Event): Any

    override fun reduce(event: Event, state: State) = Result(state).apply { reduce(event) }.build()
}
