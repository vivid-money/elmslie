package money.vivid.elmslie.core.store

import money.vivid.elmslie.core.store.Result as ReduceResult
import money.vivid.elmslie.core.store.dsl.ResultBuilder

public abstract class StateReducer<Event : Any, State : Any, Effect : Any, Command : Any> {

  // Needed to type less code
  protected inner class Result(state: State) : ResultBuilder<State, Effect, Command>(state)

  protected abstract fun Result.reduce(event: Event)

  public fun reduce(event: Event, state: State): ReduceResult<State, Effect, Command> =
    Result(state).apply { reduce(event) }.build()
}
