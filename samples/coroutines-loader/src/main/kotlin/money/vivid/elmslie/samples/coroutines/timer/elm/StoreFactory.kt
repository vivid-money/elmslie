package money.vivid.elmslie.samples.coroutines.timer.elm

import money.vivid.elmslie.core.store.ElmStore

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
