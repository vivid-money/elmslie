package vivid.money.elmslie.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.core.store.toCachedStore

/**
 * In order to access previously saved state (via [saveState]) in [storeFactory] one must use
 * SavedStateHandle.get<Bundle>(StateBundleKey)
 */
@MainThread
fun <
    Event : Any,
    Effect : Any,
    State : Any,
> Fragment.elmStore(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    defaultArgs: () -> Bundle = { arguments ?: bundleOf() },
    saveState: Bundle.(State) -> Unit = {},
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    vivid.money.elmslie.android.elmStore(
        storeFactory = storeFactory,
        key = key,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        saveState = saveState,
        defaultArgs = defaultArgs,
    )

/**
 * In order to access previously saved state (via [saveState]) in [storeFactory] one must use
 * SavedStateHandle.get<Bundle>(StateBundleKey)
 */
@MainThread
fun <
    Event : Any,
    Effect : Any,
    State : Any,
> ComponentActivity.elmStore(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    defaultArgs: () -> Bundle = { this.intent?.extras ?: bundleOf() },
    saveState: Bundle.(State) -> Unit = {},
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    vivid.money.elmslie.android.elmStore(
        storeFactory = storeFactory,
        key = key,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        saveState = saveState,
    )

@MainThread
internal fun <
    Event : Any,
    Effect : Any,
    State : Any,
> elmStore(
    key: String,
    viewModelStoreOwner: () -> ViewModelStoreOwner,
    savedStateRegistryOwner: () -> SavedStateRegistryOwner,
    defaultArgs: () -> Bundle,
    saveState: Bundle.(State) -> Unit,
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
    lazy(LazyThreadSafetyMode.NONE) {
        val factory =
            RetainedElmStoreFactory(
                stateRegistryOwner = savedStateRegistryOwner.invoke(),
                defaultArgs = defaultArgs.invoke(),
                storeFactory = storeFactory,
                saveState = saveState,
            )
        val provider = ViewModelProvider(viewModelStoreOwner.invoke(), factory)

        @Suppress("UNCHECKED_CAST")
        provider[key, RetainedElmStore::class.java].store as Store<Event, Effect, State>
    }

class RetainedElmStore<Event : Any, Effect : Any, State : Any>(
    savedStateHandle: SavedStateHandle,
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
    saveState: Bundle.(State) -> Unit,
) : ViewModel() {

    val store = storeFactory.invoke(savedStateHandle).toCachedStore().also { it.start() }

    init {
        savedStateHandle.setSavedStateProvider(StateBundleKey) {
            bundleOf().apply { saveState(store.states.value) }
        }
    }

    override fun onCleared() {
        store.stop()
    }

    companion object {

        const val StateBundleKey = "elm_store_state_bundle"
    }
}

class RetainedElmStoreFactory<Event : Any, Effect : Any, State : Any>(
    stateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    private val storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
    private val saveState: Bundle.(State) -> Unit,
) : AbstractSavedStateViewModelFactory(stateRegistryOwner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        @Suppress("UNCHECKED_CAST")
        return RetainedElmStore(
            savedStateHandle = handle,
            storeFactory = storeFactory,
            saveState = saveState,
        )
            as T
    }
}
