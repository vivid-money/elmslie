package vivid.money.elmslie.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import vivid.money.elmslie.core.store.Actor
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.Store

class ElmStoreCompat<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    reducer: StateReducer<Event, State, Effect, Command>,
    actor: ActorCompat<Command, out Event>
) : Store<Event, Effect, State> by ElmStore(
    initialState = initialState,
    reducer = reducer,
    actor = actor.toActor()
)

private fun <Command : Any, Event : Any> ActorCompat<Command, Event>.toActor() =
    Actor<Command, Event> { execute(it).flowOn(Dispatchers.IO).asObservable() }

val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.state: Flow<State>
    get() = states.asFlow()
