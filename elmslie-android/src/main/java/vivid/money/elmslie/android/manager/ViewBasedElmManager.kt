package vivid.money.elmslie.android.manager

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.core.store.Store

class ViewBasedElmManager<Event : Any, Effect : Any, State : Any>(
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
    screenLifecycle: Lifecycle,
    initEventProvider: () -> Event,
    key: String,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle,
) :
    ElmManager<Event, Effect, State>(
        key = key,
        getViewModelStoreOwner = getViewModelStoreOwner,
        getSavedStateRegistryOwner = getSavedStateRegistryOwner,
        getDefaultArgs = getDefaultArgs,
        storeFactory = storeFactory,
    ) {

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val storeStarter: ViewBasedStoreStarter<Event> =
        ViewBasedStoreStarter(
            storeProvider = { store },
            screenLifecycle = screenLifecycle,
            initEventProvider = initEventProvider,
        )
}
