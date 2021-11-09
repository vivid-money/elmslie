package vivid.money.elmslie.android.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

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
