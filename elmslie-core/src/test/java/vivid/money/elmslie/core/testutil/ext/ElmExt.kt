package vivid.money.elmslie.core.testutil.ext

import io.reactivex.Observable
import vivid.money.elmslie.core.store.Actor
import vivid.money.elmslie.core.store.Result
import vivid.money.elmslie.core.store.StateReducer

/**
 * Utility to create reducer as a function instead of a class
 */
fun <Event : Any, State : Any, Effect : Any, Command : Any> reducer(
    reduce: (event: Event, state: State) -> Result<State, Effect, Command>
) = object : StateReducer<Event, State, Effect, Command> {
    override fun reduce(event: Event, state: State): Result<State, Effect, Command> = reduce(event, state)
}

/**
 * Utility to create actor as a function instead of a class
 */
fun <Command : Any, Event : Any> actor(
    execute: (command: Command) -> Observable<Event>
) = object : Actor<Command, Event> {
    override fun execute(command: Command): Observable<Event> = execute(command)
}

/**
 * Actor that doesn't emit any events after receiving a command
 */
class NoOpActor<Command : Any, Event : Any> : Actor<Command, Event> {

    override fun execute(command: Command): Observable<Event> = Observable.never()
}

/**
 * Reducer that doesn't change state, and doesn't emit commands or effects
 */
class NoOpReducer<Event : Any, State : Any, Effect : Any, Command : Any> : StateReducer<Event, State, Effect, Command> {

    override fun reduce(event: Event, state: State): Result<State, Effect, Command> {
        return Result(state)
    }
}
