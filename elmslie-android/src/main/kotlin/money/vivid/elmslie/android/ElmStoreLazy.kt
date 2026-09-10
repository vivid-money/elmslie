package money.vivid.elmslie.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistryOwner
import money.vivid.elmslie.core.store.EffectCachingElmStore
import money.vivid.elmslie.core.store.Store
import money.vivid.elmslie.core.store.toCachedStore

/**
 * In order to access previously saved state (via [saveState]) in [storeFactory] one must use
 * SavedStateHandle.get<Bundle>(StateBundleKey)
 */
@MainThread
public fun <Event : Any, Effect : Any, State : Any> Fragment.elmStore(
  key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
  viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
  savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
  defaultArgs: () -> Bundle = { arguments ?: bundleOf() },
  saveState: Bundle.(State) -> Unit = {},
  storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
  money.vivid.elmslie.android.elmStore(
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
public fun <Event : Any, Effect : Any, State : Any> ComponentActivity.elmStore(
  key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
  viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
  savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
  defaultArgs: () -> Bundle = { this.intent?.extras ?: bundleOf() },
  saveState: Bundle.(State) -> Unit = {},
  storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
  money.vivid.elmslie.android.elmStore(
    storeFactory = storeFactory,
    key = key,
    viewModelStoreOwner = viewModelStoreOwner,
    savedStateRegistryOwner = savedStateRegistryOwner,
    defaultArgs = defaultArgs,
    saveState = saveState,
  )

@MainThread
internal fun <Event : Any, Effect : Any, State : Any> elmStore(
  key: String,
  viewModelStoreOwner: () -> ViewModelStoreOwner,
  savedStateRegistryOwner: () -> SavedStateRegistryOwner,
  defaultArgs: () -> Bundle,
  saveState: Bundle.(State) -> Unit,
  storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> =
  lazy(LazyThreadSafetyMode.NONE) {
    val storeOwner = viewModelStoreOwner.invoke()
    val factory =
      RetainedElmStoreFactory(
        stateRegistryOwner = savedStateRegistryOwner.invoke(),
        viewModelStoreOwner = storeOwner,
        defaultArgs = defaultArgs.invoke(),
        storeFactory = storeFactory,
        saveState = saveState,
      )
    val provider = ViewModelProvider(storeOwner, factory)

    @Suppress("UNCHECKED_CAST")
    provider[key, RetainedElmStore::class.java].store as Store<Event, Effect, State>
  }

public class RetainedElmStore<Event : Any, Effect : Any, State : Any>(
  savedStateHandle: SavedStateHandle,
  storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
  saveState: Bundle.(State) -> Unit,
) : ViewModel() {

  public val store: EffectCachingElmStore<Event, State, Effect> =
    storeFactory.invoke(savedStateHandle).toCachedStore().also { it.start() }

  init {
    savedStateHandle.setSavedStateProvider(StateBundleKey) {
      bundleOf().apply { saveState(store.states.value) }
    }
  }

  override fun onCleared() {
    store.stop()
  }

  public companion object {

    public const val StateBundleKey: String = "elm_store_state_bundle"
  }
}

public class RetainedElmStoreFactory<Event : Any, Effect : Any, State : Any>(
  private val stateRegistryOwner: SavedStateRegistryOwner,
  private val viewModelStoreOwner: ViewModelStoreOwner,
  private val defaultArgs: Bundle,
  private val storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
  private val saveState: Bundle.(State) -> Unit,
) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
    val savedStateExtras =
      MutableCreationExtras(extras).apply {
        set(SAVED_STATE_REGISTRY_OWNER_KEY, stateRegistryOwner)
        set(VIEW_MODEL_STORE_OWNER_KEY, viewModelStoreOwner)
        set(DEFAULT_ARGS_KEY, defaultArgs)
      }

    @Suppress("UNCHECKED_CAST")
    return RetainedElmStore(
      savedStateHandle = savedStateExtras.createSavedStateHandle(),
      storeFactory = storeFactory,
      saveState = saveState,
    )
      as T
  }
}
