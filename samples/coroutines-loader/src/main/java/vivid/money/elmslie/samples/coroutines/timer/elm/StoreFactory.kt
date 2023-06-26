package vivid.money.elmslie.samples.coroutines.timer.elm

import vivid.money.elmslie.android.StoreData

internal fun storeDataFactory(
    id: String,
    generatedId: String?,
) =
    StoreData(
        initialState =
            State(
                id = id,
                generatedId = generatedId,
            ),
        reducer = TimerReducer,
        actor = TimerActor,
        startEvent = Event.Init,
    )
