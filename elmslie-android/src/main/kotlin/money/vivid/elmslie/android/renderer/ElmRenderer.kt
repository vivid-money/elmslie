package money.vivid.elmslie.android.renderer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.store.Store

internal class ElmRenderer<Effect : Any, State : Any>(
    private val store: Store<*, Effect, State>,
    private val delegate: ElmRendererDelegate<Effect, State>,
    private val lifecycle: Lifecycle,
) {

    private val logger = ElmslieConfig.logger
    private val elmDispatcher: CoroutineDispatcher = ElmslieConfig.elmDispatcher
    private val canRender
        get() = lifecycle.currentState.isAtLeast(STARTED)

    init {
        with(lifecycle) {
            coroutineScope.launch {
                store
                    .effects
                    .flowWithLifecycle(
                        lifecycle = lifecycle,
                        minActiveState = RESUMED,
                    )
                    .collect { effect -> catchEffectErrors { delegate.handleEffect(effect) } }
            }
            coroutineScope.launch {
                store
                    .states
                    .flowWithLifecycle(
                        lifecycle = lifecycle,
                        minActiveState = STARTED,
                    )
                    .map { state ->
                        val list = mapListItems(state)
                        state to list
                    }
                    .catch {
                        logger.fatal(
                            message = "Crash while mapping state",
                            error = it,
                        )
                    }
                    .flowOn(elmDispatcher)
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
            logger.fatal(
                message = "Crash while rendering state",
                error = t,
            )
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchEffectErrors(action: () -> T?) =
        try {
            action()
        } catch (t: Throwable) {
            logger.fatal(
                message = "Crash while handling effect",
                error = t,
            )
        }
}
