package vivid.money.elmslie.android.renderer

import androidx.lifecycle.*
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.config.ElmslieConfig

class ElmRenderer<Effect : Any, State : Any>(
    private val delegate: ElmRendererDelegate<Effect, State>,
    private val screenLifecycle: Lifecycle,
) {

    private val store by fastLazy { delegate.store }
    private val logger = ElmslieConfig.logger
    private val ioDispatcher: CoroutineDispatcher = ElmslieConfig.ioDispatchers
    private val canRender
        get() = screenLifecycle.currentState.isAtLeast(STARTED)

    init {
        with(screenLifecycle) {
            coroutineScope.launch {
                store
                    .effects()
                    .flowWithLifecycle(
                        lifecycle = screenLifecycle,
                        minActiveState = RESUMED,
                    )
                    .collect { effect -> catchEffectErrors { delegate.handleEffect(effect) } }
            }
            coroutineScope.launch {
                store
                    .states()
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
