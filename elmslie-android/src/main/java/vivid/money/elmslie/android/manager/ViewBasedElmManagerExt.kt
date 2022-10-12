package vivid.money.elmslie.android.manager

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

fun <Event : Any, Effect : Any, State : Any> Fragment.createElmManager(
    storeCreator: () -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
    storeHolderFactory: (() -> Store<Event, Effect, State>) -> StoreHolder<Event, Effect, State> = {
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = it,
        )
    },
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeProvider = { storeHolderFactory.invoke { storeCreator.invoke() }.store },
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
    )

fun <Event : Any, Effect : Any, State : Any> ComponentActivity.createElmManager(
    storeCreator: () -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
    storeHolderFactory: (() -> Store<Event, Effect, State>) -> StoreHolder<Event, Effect, State> = {
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = it,
        )
    },
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeProvider = { storeHolderFactory.invoke { storeCreator.invoke() }.store },
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
    )
