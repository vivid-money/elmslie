package vivid.money.elmslie.android.manager

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store

abstract class ElmManager<Event : Any, Effect : Any, State : Any>(
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
    key: String,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle,
) {
    val store: Store<Event, Effect, State> by fastLazy {
        val factory =
            RetainedViewModelFactory(
                stateRegistryOwner = getSavedStateRegistryOwner.invoke(),
                defaultArgs = getDefaultArgs.invoke(),
                storeFactory = storeFactory,
            )
        val provider = ViewModelProvider(getViewModelStoreOwner.invoke(), factory)

        @Suppress("UNCHECKED_CAST")
        provider.get(key, RetainedViewModel::class.java).store as Store<Event, Effect, State>
    }
}

private class RetainedViewModel<Event : Any, Effect : Any, State : Any>(
    savedStateHandle: SavedStateHandle,
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
) : ViewModel() {

    val store by fastLazy { storeFactory.invoke(savedStateHandle).apply { start() } }

    override fun onCleared() {
        store.stop()
    }
}

private class RetainedViewModelFactory<Event : Any, Effect : Any, State : Any>(
    stateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    private val storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
) : AbstractSavedStateViewModelFactory(stateRegistryOwner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        @Suppress("UNCHECKED_CAST") return RetainedViewModel(handle, storeFactory) as T
    }
}
