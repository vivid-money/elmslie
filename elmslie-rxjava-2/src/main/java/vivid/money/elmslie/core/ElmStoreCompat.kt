package vivid.money.elmslie.core

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

private fun <Command : Any, Event : Any> ActorCompat<Command, Event>
        .toActor() = Actor<Command, Event> { execute(it).toV3() }
