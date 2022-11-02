package vivid.money.elmslie.android.fabric

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import vivid.money.elmslie.core.store.Store

@MainThread
fun <
    Event : Any,
    Effect : Any,
    State : Any,
> elmStore(
    key: String,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle,
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    lazy(LazyThreadSafetyMode.NONE) {
        val factory =
            RetainedElmStoreFactory(
                stateRegistryOwner = getSavedStateRegistryOwner.invoke(),
                defaultArgs = getDefaultArgs.invoke(),
                storeFactory = storeFactory,
            )
        val provider = ViewModelProvider(getViewModelStoreOwner.invoke(), factory)

        @Suppress("UNCHECKED_CAST")
        provider.get(key, RetainedElmStore::class.java).store as Store<Event, Effect, State>
    }

@MainThread
fun <
    Event : Any,
    Effect : Any,
    State : Any,
> Fragment.elmStore(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    getViewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    getDefaultArgs: () -> Bundle = { arguments ?: Bundle() },
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    vivid.money.elmslie.android.fabric.elmStore(
        storeFactory = storeFactory,
        key = key,
        getViewModelStoreOwner = getViewModelStoreOwner,
        getSavedStateRegistryOwner = getSavedStateRegistryOwner,
        getDefaultArgs = getDefaultArgs,
    )

@MainThread
fun <
    Event : Any,
    Effect : Any,
    State : Any,
> ComponentActivity.elmStore(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    getViewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    getDefaultArgs: () -> Bundle = { this.intent?.extras ?: Bundle() },
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    vivid.money.elmslie.android.fabric.elmStore(
        storeFactory = storeFactory,
        key = key,
        getViewModelStoreOwner = getViewModelStoreOwner,
        getSavedStateRegistryOwner = getSavedStateRegistryOwner,
        getDefaultArgs = getDefaultArgs,
    )

class RetainedElmStore<Event : Any, Effect : Any, State : Any>(
    savedStateHandle: SavedStateHandle,
    storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
) : ViewModel() {

    val store = storeFactory.invoke(savedStateHandle).also { it.start() }

    override fun onCleared() {
        store.stop()
    }
}

class RetainedElmStoreFactory<Event : Any, Effect : Any, State : Any>(
    stateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    private val storeFactory: (SavedStateHandle) -> Store<Event, Effect, State>,
) : AbstractSavedStateViewModelFactory(stateRegistryOwner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        @Suppress("UNCHECKED_CAST") return RetainedElmStore(handle, storeFactory) as T
    }
}
