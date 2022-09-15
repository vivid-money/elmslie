package vivid.money.elmslie.coroutines

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import vivid.money.elmslie.core.switcher.Switcher

/** @see [Switcher] */
@Suppress("TooGenericExceptionCaught", "RethrowCaughtException")
fun <Event : Any> Switcher.switch(
    delayMillis: Long = 0,
    action: () -> Flow<Event>,
): Flow<Event> = switchInternal(
    delayMillis = delayMillis,
    action = action,
)

fun Switcher.cancel(delayMillis: Long = 0) = switch(delayMillis = delayMillis) { emptyFlow() }
