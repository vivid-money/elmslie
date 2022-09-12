package vivid.money.elmslie.rx2

import io.reactivex.Observable
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import vivid.money.elmslie.core.store.DefaultActor
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.Store

/** A [Store] implementation that uses RxJava2 for multithreading */
class ElmStoreCompat<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    reducer: StateReducer<Event, State, Effect, Command>,
    actor: Actor<Command, out Event>
) :
    Store<Event, Effect, State> by ElmStore(
        initialState = initialState,
        reducer = reducer,
        actor = actor.toActor()
    )

private fun <Command : Any, Event : Any> Actor<Command, Event>.toActor() =
    DefaultActor<Command, Event> { command ->
        execute(command).asFlow()
    }

/** An extension for accessing [Store] states as an [Observable] */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.states: Observable<State>
    get() = states().asObservable()

/** An extension for accessing [Store] effects as an [Observable] */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.effects: Observable<Effect>
    get() = effects().asObservable()
