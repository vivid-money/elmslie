package vivid.money.elmslie.samples.coroutines.timer.elm

import vivid.money.elmslie.core.store.ElmStore

internal fun storeFactory(
    id: String,
    generatedId: String?,
) =
    ElmStore(
        initialState =
            State(
                id = id,
                generatedId = generatedId,
            ),
        reducer = TimerReducer,
        actor = TimerActor,
        startEvent = Event.Init,
    )
