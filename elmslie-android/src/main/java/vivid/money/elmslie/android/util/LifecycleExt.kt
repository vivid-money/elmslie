package vivid.money.elmslie.android.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import vivid.money.elmslie.core.disposable.Disposable

/**
 * Executes a given [action] at [ON_CREATE] event of the provided [Lifecycle]
 */
fun Lifecycle.onCreate(
    action: () -> Unit
) = addObserver(object : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == ON_CREATE) action()
    }
})

/**
 * Binds the specified [disposableProvider] to the lifecycle [state]
 */
fun Lifecycle.bindToState(
    state: Lifecycle.State,
    disposableProvider: () -> Disposable
) = addObserver(object : LifecycleEventObserver {
    private var disposable: Disposable? = null
    private val startEvent = Lifecycle.Event.upTo(state)
    private val endEvent = Lifecycle.Event.downFrom(state)
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == startEvent) disposable = disposableProvider()
        if (event == endEvent) disposable?.dispose().also { disposable = null }
    }
})
