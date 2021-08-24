package vivid.money.elmslie.android.screen

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import vivid.money.elmslie.android.processdeath.ProcessDeathDetector
import vivid.money.elmslie.android.processdeath.StopElmOnProcessDeath
import vivid.money.elmslie.core.config.ElmslieConfig

class ElmScreen<Event : Any, Effect : Any, State : Any>(
    private val delegate: ElmDelegate<Event, Effect, State>,
    screenLifecycle: Lifecycle,
    private val activityProvider: () -> Activity,
) {

    private val logger = ElmslieConfig.logger
    private val handler = Handler(Looper.getMainLooper())
    val store
        get() = delegate.storeHolder.store

    private var effectsDisposable: Disposable? = null
    private var statesDisposable: Disposable? = null
    private var isAfterProcessDeath: Boolean = false
    private val isScreenRenderable: Boolean get() = statesDisposable?.isDisposed == false

    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun onCreate() {
            isAfterProcessDeath = ProcessDeathDetector.isRestoringAfterProcessDeath
            if (!store.isStarted && isAllowedToRunMvi()) {
                store.accept(delegate.initEvent)
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onStart() {
            statesDisposable = observeStates()
            val lastState = store.states.blockingFirst()
            delegate.render(lastState)
            delegate.renderList(lastState, delegate.mapList(lastState))
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
            statesDisposable = null
        }
    }

    init {
        screenLifecycle.addObserver(lifecycleObserver)
    }

    private fun isAllowedToRunMvi() =
        delegate.isAllowedToRunMvi && (!isAfterProcessDeath || activityProvider() !is StopElmOnProcessDeath)

    private fun observeStates() = store.states
        .skip(1) // skipped first state, because we need to avoid rendering initial state twice
        .observeOn(Schedulers.computation())
        .flatMapMaybe { if (isScreenRenderable) Maybe.just(it to delegate.mapList(it)) else Maybe.empty() }
        .doOnError { logger.fatal("Crash while rendering state", it) }
        .retry()
        .subscribe { (state, list) ->
            handler.removeCallbacksAndMessages(null)
            handler.post {
                if (isScreenRenderable) {
                    delegate.render(state)
                    delegate.renderList(state, list)
                }
            }
        }

    private fun observeEffects(): Disposable = store.effects
        .doOnError { logger.fatal("Crash while handling effect", it) }
        .retry()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { delegate.handleEffect(it) }
}
