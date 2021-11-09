package vivid.money.elmslie.android.util

import android.os.Handler
import android.os.Looper

/**
 * Runs an action on the handler thread as soon as possible without blocking.
 * Ensures that only one action at a time is scheduled for executing.
 */
fun Handler.postSingle(action: () -> Unit) {
    removeCallbacksAndMessages(null)
    if (looper == Looper.myLooper()) {
        action()
    } else {
        post { action() }
    }
}
