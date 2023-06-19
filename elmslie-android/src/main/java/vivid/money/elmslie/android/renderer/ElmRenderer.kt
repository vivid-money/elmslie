package vivid.money.elmslie.android.renderer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.Store

@Suppress("LongParameterList")
@MainThread
fun <
        Event : Any,
        Effect : Any,
        State : Any,
        > elmStoreWithRenderer(
    lifecycleOwner: LifecycleOwner,
    key: String = lifecycleOwner::class.java.canonicalName ?: lifecycleOwner::class.java.simpleName,
    elmRenderer: ElmRendererDelegate<Effect, State>,
    viewModelStoreOwner: () -> ViewModelStoreOwner,
    savedStateRegistryOwner: () -> SavedStateRegistryOwner,
    defaultArgs: () -> Bundle,
    saveState: Bundle.(State) -> Unit = {},
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> {
    val lazyStore = vivid.money.elmslie.android.elmStore(
        storeFactory = storeFactory,
        key = key,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        saveState = saveState,
        defaultArgs = defaultArgs,
    )
    with(lifecycleOwner) {
        lifecycleScope.launch {
            withCreated {
                ElmRenderer(
                    lazyStore.value,
                    elmRenderer,
                    lifecycle,
                )
            }
        }
    }
    return lazyStore
}

@Suppress("LongParameterList")
@MainThread
fun <
        Event : Any,
        Effect : Any,
        State : Any,
        > Fragment.elmStoreWithRenderer(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    elmRenderer: ElmRendererDelegate<Effect, State>,
    viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    defaultArgs: () -> Bundle = { arguments ?: bundleOf() },
    saveState: Bundle.(State) -> Unit = {},
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> {
    return elmStoreWithRenderer(
        lifecycleOwner = this,
        key = key,
        elmRenderer = elmRenderer,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        saveState = saveState,
        storeFactory = storeFactory
    )
}

@Suppress("LongParameterList")
@MainThread
fun <
        Event : Any,
        Effect : Any,
        State : Any,
        > ComponentActivity.elmStoreWithRenderer(
    key: String = this::class.java.canonicalName ?: this::class.java.simpleName,
    elmRenderer: ElmRendererDelegate<Effect, State>,
    viewModelStoreOwner: () -> ViewModelStoreOwner = { this },
    savedStateRegistryOwner: () -> SavedStateRegistryOwner = { this },
    defaultArgs: () -> Bundle = { intent?.extras ?: bundleOf() },
    saveState: Bundle.(State) -> Unit = {},
    storeFactory: SavedStateHandle.() -> Store<Event, Effect, State>,
): Lazy<Store<Event, Effect, State>> {
    return elmStoreWithRenderer(
        lifecycleOwner = this,
        key = key,
        elmRenderer = elmRenderer,
        viewModelStoreOwner = viewModelStoreOwner,
        savedStateRegistryOwner = savedStateRegistryOwner,
        defaultArgs = defaultArgs,
        saveState = saveState,
        storeFactory = storeFactory
    )
}

internal class ElmRenderer<Effect : Any, State : Any>(
    private val store: Store<*, Effect, State>,
    private val delegate: ElmRendererDelegate<Effect, State>,
    private val screenLifecycle: Lifecycle,
) {

    private val logger = ElmslieConfig.logger
    private val ioDispatcher: CoroutineDispatcher = ElmslieConfig.ioDispatchers
    private val canRender
        get() = screenLifecycle.currentState.isAtLeast(STARTED)

    init {
        with(screenLifecycle) {
            coroutineScope.launch {
                store
                    .effects
                    .flowWithLifecycle(
                        lifecycle = screenLifecycle,
                        minActiveState = RESUMED,
                    )
                    .collect { effect -> catchEffectErrors { delegate.handleEffect(effect) } }
            }
            coroutineScope.launch {
                store
                    .states
                    .flowWithLifecycle(
                        lifecycle = screenLifecycle,
                        minActiveState = STARTED,
                    )
                    .map { state ->
                        val list = mapListItems(state)
                        state to list
                    }
                    .catch { logger.fatal("Crash while mapping state", it) }
                    .flowOn(ioDispatcher)
                    .collect { (state, listItems) ->
                        catchStateErrors {
                            if (canRender) {
                                delegate.renderList(state, listItems)
                                delegate.render(state)
                            }
                        }
                    }
            }
        }
    }

    private fun mapListItems(state: State) =
        catchStateErrors { delegate.mapList(state) } ?: emptyList()

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchStateErrors(action: () -> T?) =
        try {
            action()
        } catch (t: Throwable) {
            logger.fatal("Crash while rendering state", t)
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchEffectErrors(action: () -> T?) =
        try {
            action()
        } catch (t: Throwable) {
            logger.fatal("Crash while handling effect", t)
        }
}
