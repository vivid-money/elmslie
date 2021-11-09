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
fun Switcher.cancel(delayMillis: Long = 0) = this.switch(delayMillis) { flowOf() }

/**
 * @see [Switcher]
 */
fun <Event : Any> Switcher.switch(
    delayMillis: Long = 0,
    action: () -> Flow<Event>,
) = channelFlow {
    val disposable = switchInternal(delayMillis) {
        val job = GlobalScope.launch(Dispatchers.Unconfined) {
            action().collect { trySend(it) }
        }
        Disposable { job.cancel() }
    }
    awaitClose { disposable.dispose() }
}
