package vivid.money.elmslie.android.screen

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector.isRestoringAfterProcessDeath
import vivid.money.elmslie.android.processdeath.StopElmOnProcessDeath
import vivid.money.elmslie.android.util.bindToState
import vivid.money.elmslie.android.util.onCreate
import vivid.money.elmslie.android.util.postSingle
import vivid.money.elmslie.core.config.ElmslieConfig

class ElmScreen<Event : Any, Effect : Any, State : Any>(
    private val delegate: ElmDelegate<Event, Effect, State>,
    private val screenLifecycle: Lifecycle,
    private val activityProvider: () -> Activity,
) {

    val store get() = delegate.storeHolder.store

    private val stateHandler = Handler(Looper.getMainLooper())
    private val effectHandler = Handler(Looper.getMainLooper())
    private val logger = ElmslieConfig.logger
    private var isAfterProcessDeath = false
    private val isRenderable get() = screenLifecycle.currentState.isAtLeast(STARTED)

    init {
        with(screenLifecycle) {
            onCreate(::saveProcessDeathState)
            onCreate(::triggerInitEventIfNecessary)
            bindToState(RESUMED, ::observeEffects)
            bindToState(STARTED, ::observeStates)
        }
    }

    private fun observeEffects() = store.effects {
        effectHandler.post {
            catchEffectErrors {
                delegate.handleEffect(it)
            }
        }
    }

    private fun observeStates() = store.states {
        val list = mapListItems(it)
        stateHandler.postSingle { renderListItems(it, list) }
    }

    private fun mapListItems(state: State) = catchStateErrors {
        delegate.mapList(state)
    } ?: emptyList()

    private fun renderListItems(state: State, list: List<Any>) = catchStateErrors {
        if (isRenderable) {
            delegate.renderList(state, list)
            delegate.render(state)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchStateErrors(action: () -> T?) = try {
        action()
    } catch (t: Throwable) {
        logger.fatal("Crash while rendering state", t)
        null
    }

    @Suppress("TooGenericExceptionCaught")
    private fun <T> catchEffectErrors(action: () -> T?) = try {
        action()
    } catch (t: Throwable) {
        logger.fatal("Crash while handling effect", t)
    }

    private fun saveProcessDeathState() {
        isAfterProcessDeath = isRestoringAfterProcessDeath
    }

    private fun triggerInitEventIfNecessary() {
        if (!delegate.storeHolder.isStarted && isAllowedToRun()) {
            store.accept(delegate.initEvent)
        }
    }

    private fun isAllowedToRun() =
        !isAfterProcessDeath || activityProvider() !is StopElmOnProcessDeath
}
