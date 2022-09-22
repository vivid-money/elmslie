package vivid.money.elmslie.coroutines

import kotlinx.coroutines.flow.Flow
import vivid.money.elmslie.core.store.DefaultActor
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.Store

/** Compatibility [Store] implementation applying coroutines for multithreading. */
class ElmStoreCompat<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    reducer: StateReducer<Event, State, Effect, Command>,
    actor: Actor<Command, out Event>,
    startEvent: Event? = null,
) :
    Store<Event, Effect, State> by ElmStore(
        initialState = initialState,
        reducer = reducer,
        actor = actor.toDefaultActor(),
        startEvent = startEvent,
    )

@Suppress("TooGenericExceptionCaught", "RethrowCaughtException")
private fun <Command : Any, Event : Any> Actor<Command, Event>.toDefaultActor() =
    DefaultActor<Command, Event> { command -> execute(command) }

/** Extension for accessing [Store] states as a [Flow]. */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.states: Flow<State>
    get() = states()

/** Extension for accessing [Store] effects as a [Flow]. */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.effects: Flow<Effect>
    get() = effects()
