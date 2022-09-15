package vivid.money.elmslie.core.switcher

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import vivid.money.elmslie.core.store.DefaultActor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Allows to execute requests for [DefaultActor] implementations in a switching manner. Each request
 * will cancel the previous one.
 *
 * Example:
 * ```
 * private val switcher = Switcher()
 *
 * override fun execute(command: Command) = when (command) {
 *    is MyCommand -> switcher.switchInternal() {
 *        Observable.just(123)
 *    }
 * }
 * ```
 */
class Switcher {

    private var currentChannel: SendChannel<*>? = null
    private val lock = ReentrantLock()
    /**
     * Collect given flow as a job and cancels all previous ones.
     *
     * @param delayMillis operation delay measured with milliseconds. Can be specified to debounce
     * existing requests.
     * @param action actual event source
     */
    fun <Event : Any> switchInternal(
        delayMillis: Long = 0,
        action: () -> Flow<Event>,
    ): Flow<Event> {
        return callbackFlow {
            lock.withLock {
                currentChannel?.close()
                currentChannel = channel
            }

            delay(delayMillis)

            action.invoke().collect { send(it) }

            channel.close()
        }
    }
}
