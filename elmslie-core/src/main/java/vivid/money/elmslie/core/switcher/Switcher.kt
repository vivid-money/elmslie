package vivid.money.elmslie.core.switcher

import kotlinx.coroutines.*
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.DefaultActor

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

    @Volatile private var currentJob: Job? = null
    private val switcherScope: CoroutineScope = CoroutineScope(ElmslieConfig.ioDispatchers)

    /**
     * Executes [action] as a job and cancels all previous ones.
     *
     * @param delayMillis operation delay measured with milliseconds. Can be specified to debounce existing requests.
     * @param action new operation to be executed.
     */
    fun switchInternal(
        delayMillis: Long = 0,
        action: suspend () -> Unit,
    ): Job? {
        currentJob?.cancel()
        return try {
                switcherScope.launch {
                    delay(delayMillis)
                    action.invoke()
                }
            } catch (_: CancellationException) {
                currentJob?.cancel()
                null
            }
            .also { currentJob = it }
    }
}
