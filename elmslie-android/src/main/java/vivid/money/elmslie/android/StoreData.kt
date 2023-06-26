package vivid.money.elmslie.android

import vivid.money.elmslie.core.store.Actor
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.StoreListener

data class StoreData<Event : Any, State : Any, Effect : Any, Command : Any>(
    val initialState: State,
    val reducer: StateReducer<Event, State, Effect, Command>,
    val actor: Actor<Command, out Event>,
    val storeListeners: Set<StoreListener<Event, State, Effect, Command>>? = null,
    val startEvent: Event? = null,
)
