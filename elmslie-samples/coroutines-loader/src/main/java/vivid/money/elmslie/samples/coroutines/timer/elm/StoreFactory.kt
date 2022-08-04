package vivid.money.elmslie.samples.coroutines.timer.elm

import vivid.money.elmslie.coroutines.ElmStoreCompat

internal fun storeFactory() = ElmStoreCompat(
    State(),
    TimerReducer,
    TimerActor
)
