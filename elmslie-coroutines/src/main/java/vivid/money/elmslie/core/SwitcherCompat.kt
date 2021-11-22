package vivid.money.elmslie.core

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.switcher.Switcher

/**
 * Cancels all scheduled actions after [delayMillis] pass.
 *
 * @param delayMillis Cancellation delay measured with milliseconds.
 */
fun Switcher.cancel(delayMillis: Long = 0) = switch(delayMillis) { flowOf() }

/**
 * @see [Switcher]
 */
@Suppress("TooGenericExceptionCaught")
fun <Event : Any> Switcher.switch(
    delayMillis: Long = 0,
    action: () -> Flow<Event>,
): Flow<Event> = channelFlow {
    try {
        val disposable = switchInternal(delayMillis) {
            val job = launch {
                action().collect { trySend(it) }
            }
            Disposable(job::cancel)
        }
        awaitClose(disposable::dispose)
    } catch (t: Throwable) {
        // Next action cancelled this before starting. Or some error happened while running action.
        close(t)
    }
}
