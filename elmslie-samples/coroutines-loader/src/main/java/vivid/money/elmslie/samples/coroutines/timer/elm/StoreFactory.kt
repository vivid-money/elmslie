package vivid.money.elmslie.samples.coroutines.timer.elm

import vivid.money.elmslie.core.store.ElmStore

internal fun storeFactory(id: String) = ElmStore(
    initialState = State(
        id = id,
    ),
    reducer = TimerReducer,
    actor = TimerActor,
    startEvent = Event.Init,
)
