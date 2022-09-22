package vivid.money.elmslie.android.manager

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

fun <Event : Any, Effect : Any, State : Any> Fragment.createElmManager(
    storeHolderFactory: (() -> Store<Event, Effect, State>) -> StoreHolder<Event, Effect, State> = {
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = it,
        )
    },
    storeCreator: () -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeHolderFactory = storeHolderFactory,
        storeCreator = storeCreator,
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
        activityProvider = { requireActivity() },
    )

fun <Event : Any, Effect : Any, State : Any> ComponentActivity.createElmManager(
    storeHolderFactory: (() -> Store<Event, Effect, State>) -> StoreHolder<Event, Effect, State> = {
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = it,
        )
    },
    storeCreator: () -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeHolderFactory = storeHolderFactory,
        storeCreator = storeCreator,
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
        activityProvider = { this },
    )
