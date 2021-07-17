package vivid.money.elmslie.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import vivid.money.elmslie.core.switcher.Switcher

/**
 * @see [Switcher]
 */
class SwitcherCompat {

    private val switcher = Switcher()

    /**
     * Cancels current action in switcher after [delayMillis]
     *
     * @param delayMillis Cancellation delay in milliseconds
     */
    fun cancel(delayMillis: Long = 0) = switch(delayMillis) { flowOf() }

    /**
     * @see [Switcher]
     */
    fun <Event : Any> switch(
        delayMillis: Long = 0,
        action: () -> Flow<Event>,
    ) = switcher
        .observable(delayMillis) { action().asObservable() }
        .asFlow()
}