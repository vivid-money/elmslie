package vivid.money.elmslie.android.screen

import android.app.Activity
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
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector.isRestoringAfterProcessDeath
import vivid.money.elmslie.android.processdeath.StopElmOnProcessDeath
import vivid.money.elmslie.core.config.ElmslieConfig

class ElmScreen<Event : Any, Effect : Any, State : Any>(
    private val delegate: ElmDelegate<Event, Effect, State>,
    private val screenLifecycle: Lifecycle,
    private val activityProvider: () -> Activity,
) {

    val store
        get() = delegate.storeHolder.store

    private val logger = ElmslieConfig.logger
    private val ioDispatcher: CoroutineDispatcher = ElmslieConfig.ioDispatchers
    private var isAfterProcessDeath = false
    private val canRender
        get() = screenLifecycle.currentState.isAtLeast(STARTED)

    init {
        with(screenLifecycle) {
            coroutineScope.launchWhenCreated {
                saveProcessDeathState()
                triggerInitEventIfNecessary()
            }
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

    private fun mapListItems(state: State): List<Any> =
        catchStateErrors { delegate.mapList(state) } ?: emptyList()

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchStateErrors(action: () -> T?): T? =
        try {
            action()
        } catch (t: Throwable) {
            logger.fatal("Crash while rendering state", t)
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchEffectErrors(action: () -> T?) {
        try {
            action()
        } catch (t: Throwable) {
            logger.fatal("Crash while handling effect", t)
        }
    }

    private fun saveProcessDeathState() {
        isAfterProcessDeath = isRestoringAfterProcessDeath
    }

    private fun triggerInitEventIfNecessary() {
        if (!delegate.storeHolder.isStarted && isAllowedToRun()) {
            store.accept(delegate.initEvent)
        }
    }

    private fun isAllowedToRun(): Boolean =
        !isAfterProcessDeath || activityProvider() !is StopElmOnProcessDeath
}
