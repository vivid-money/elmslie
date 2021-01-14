package vivid.money.elmslie.android.screen

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.android.processdeath.MviSurvivesProcessDeath
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.Store

internal class ScreenMvi<Event : Any, Effect : Any, State : Any, MviStore : Store<Event, Effect, State>>(
    private val delegate: MviDelegate<Event, Effect, State, MviStore>,
    private val activityProvider: () -> Activity
) {

    private val logger = ElmslieConfig.logger
    val store: MviStore by fastLazy { delegate.createStore() }

    private var effectsDisposable: Disposable? = null
    private var statesDisposable: Disposable? = null
    private var isAfterProcessDeath: Boolean = false

    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            isAfterProcessDeath = ProcessDeathDetector.isRestoringAfterProcessDeath
            if (isAllowedToRunMvi()) {
                store.accept(delegate.initEvent)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            statesDisposable = observeStates()
            val initialState = store.states.blockingFirst()
            delegate.render(initialState)
            delegate.renderList(delegate.mapList(initialState))
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume() {
            effectsDisposable = observeEffects()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause() {
            effectsDisposable?.dispose()
            effectsDisposable = null
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop() {
            statesDisposable?.dispose()
            effectsDisposable = null
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            if (isAllowedToRunMvi()) {
                store.dispose()
            }
        }
    }

    init {
        delegate.screenLifecycle.addObserver(lifecycleObserver)
    }

    private fun isAllowedToRunMvi() = !isAfterProcessDeath || activityProvider() is MviSurvivesProcessDeath

    private fun observeStates() = store.states
        .skip(1) // skipped first state, because we need to avoid rendering initial state twice
        .observeOn(Schedulers.computation())
        .map { state -> state to delegate.mapList(state) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ (state, list) ->
            delegate.render(state)
            delegate.renderList(list)
        }, {
            logger.fatal("Crash while rendering state", it)
        })

    private fun observeEffects(): Disposable = store.effects
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            delegate.handleEffect(it)
        }, {
            logger.fatal("Crash while handling effect", it)
        })
}
