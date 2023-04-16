package vivid.money.elmslie.samples.coroutines.timer.elm

import vivid.money.elmslie.coroutines.ElmStoreCompat

internal fun storeFactory(id: String) = ElmStoreCompat(
    initialState = State(
        id = id,
    ),
    reducer = TimerReducer,
    actor = TimerActor,
    startEvent = Event.Init,
)
