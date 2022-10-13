package vivid.money.elmslie.android.manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import vivid.money.elmslie.core.store.Store

fun <
    Event : Any,
    Effect : Any,
    State : Any,
> Fragment.createElmManager(
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
    key: String,
    getDefaultArgs: () -> Bundle = { arguments ?: Bundle() },
    getViewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeFactory = storeFactory,
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
        key = key,
        getViewModelStoreOwner = getViewModelStoreOwner,
        getSavedStateRegistryOwner = getSavedStateRegistryOwner,
        getDefaultArgs = getDefaultArgs,
    )

fun <
    Event : Any,
    Effect : Any,
    State : Any,
> ComponentActivity.createElmManager(
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
    initEventProvider: () -> Event,
    key: String,
    getDefaultArgs: () -> Bundle = { this.intent?.extras ?: Bundle() },
    getViewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
): ViewBasedElmManager<Event, Effect, State> =
    ViewBasedElmManager(
        storeFactory = storeFactory,
        screenLifecycle = lifecycle,
        initEventProvider = initEventProvider,
        key = key,
        getViewModelStoreOwner = getViewModelStoreOwner,
        getSavedStateRegistryOwner = getSavedStateRegistryOwner,
        getDefaultArgs = getDefaultArgs,
    )
