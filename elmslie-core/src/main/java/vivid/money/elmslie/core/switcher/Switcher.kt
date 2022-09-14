package vivid.money.elmslie.core.switcher

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    /**
     * Collect given flow as a job and cancels all previous ones.
     *
     * @param coroutineScope outer scope where the result Flow will be collected.
     * @param delayMillis operation delay measured with milliseconds. Can be specified to debounce
     * existing requests.
     * @param onEach callback for successful emission
     * @param onComplete callback when flow is finished emission
     * @param onError callback for failed emission
     */
    fun <Event : Any> Flow<Event>.switchInternal(
        coroutineScope: CoroutineScope,
        delayMillis: Long = 0,
        onEach: (Event) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit,
    ): Job {
        currentJob?.cancel()
        return coroutineScope
            .launch {
                delay(delayMillis)
                this@switchInternal.cancellable()
                    .catch { onError(it) }
                    .collect { event -> onEach.invoke(event) }
                onComplete.invoke()
            }
            .also { currentJob = it }
    }

    fun clear(job: Job) {
        // clear reference only if job is cancelled by cancelling outer scope.
        if (currentJob == job) {
            currentJob = null
        }
    }
}
