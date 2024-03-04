package money.vivid.elmslie.core.store

import money.vivid.elmslie.core.store.dsl.ResultBuilder

abstract class StateReducer<Event : Any, State : Any, Effect : Any, Command : Any> {

    // Needed to type less code
    protected inner class Result(state: State) : ResultBuilder<State, Effect, Command>(state)

    protected abstract fun Result.reduce(event: Event)

    fun reduce(event: Event, state: State) = Result(state).apply { reduce(event) }.build()
}
