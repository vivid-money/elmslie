package vivid.money.elmslie.core.switcher

import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.disposable.CompositeDisposable
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.store.DefaultActor
import java.util.concurrent.CancellationException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Allows to execute requests for [DefaultActor] implementations in a switching manner.
 * Each request will cancel the previous one.
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

    private val disposable = CompositeDisposable()
    private val tasks = mutableSetOf<ScheduledFuture<*>>()
    private val service = ElmslieConfig.backgroundExecutor

    /**
     * Executes [action] and cancels all previous requests scheduled on this [Switcher].
     *
     * @param delayMillis Operation delay measured with milliseconds.
     *                    Can be specified to debounce existing requests.
     * @param action New operation to be executed.
     */
    fun switchInternal(
        delayMillis: Long = 0,
        action: () -> Disposable,
    ): Disposable {
        disposable.clear()
        synchronized(tasks) {
            tasks.onEach { task -> task.cancel(true) }.clear()
        }
        val future = service.schedule(
            { disposable.add(action()) },
            delayMillis,
            TimeUnit.MILLISECONDS
        )
        synchronized(tasks) {
            tasks.add(future)
        }
        return Disposable { future.cancel(true) }
    }

    /**
     * Awaits completion of all scheduled background tasks.
     */
    fun await(
        delay: Long = 1,
        timeUnit: TimeUnit = TimeUnit.DAYS
    ) = tasks.forEach { task ->
        try {
            task.get(delay, timeUnit)
        } catch (_: CancellationException) {
            // Expected state
        }
    }
}
