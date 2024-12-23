package money.vivid.elmslie.core.switcher

import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import money.vivid.elmslie.core.store.Actor

/**
 * Allows to execute requests for [Actor] implementations in a switching manner. Each request
 * will cancel the previous one.
 *
 * Example:
 * ```
 * private val switcher = Switcher()
 *
 * override fun execute(command: Command): Flow<*> = when (command) {
 *    is MyCommand -> switcher.switch {
 *        flowOf(123)
 *    }
 * }
 * ```
 */
@Deprecated("Will be internal. Consider migrate it's usage to 'Actor.asSwitchFlow'")
class Switcher {

    private var currentChannel: SendChannel<*>? = null
    private val lock = Mutex()

    /**
     * Collect given flow as a job and cancels all previous ones.
     *
     * @param delayMillis operation delay measured with milliseconds. Can be specified to debounce
     * existing requests.
     * @param action actual event source
     */
    fun <Event : Any> switch(
        delayMillis: Long = 0,
        action: () -> Flow<Event>,
    ): Flow<Event> {
        return callbackFlow {
            lock.withLock {
                currentChannel?.close()
                currentChannel = channel
            }

            delay(delayMillis)

            action.invoke()
                .onEach { send(it) }
                .catch { close(it) }
                .collect()

            channel.close()
        }
    }

    suspend fun cancel(
        delayMillis: Long = 0,
    ) {
        delay(delayMillis)
        lock.withLock {
            currentChannel?.close()
            currentChannel = null
        }
    }
}
