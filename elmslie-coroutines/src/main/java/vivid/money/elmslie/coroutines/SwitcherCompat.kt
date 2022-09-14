package vivid.money.elmslie.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import vivid.money.elmslie.core.switcher.Switcher

/** @see [Switcher] */
@Suppress("TooGenericExceptionCaught", "RethrowCaughtException")
fun <Event : Any> Switcher.switch(
    delayMillis: Long = 0,
    action: () -> Flow<Event>,
): Flow<Event> = callbackFlow {
    val job =
        action
            .invoke()
            .switchInternal(
                coroutineScope = this,
                delayMillis = delayMillis,
                onEach = { trySend(it) },
                onComplete = { close() },
                onError = { cancel(CancellationException("", it)) },
            )
    job.invokeOnCompletion {
        if (it is CancellationException) {
            close()
        }
    }

    awaitClose { this@switch.clear(job) }
}

fun Switcher.cancel(delayMillis: Long = 0) = switch(delayMillis = delayMillis) { emptyFlow() }
