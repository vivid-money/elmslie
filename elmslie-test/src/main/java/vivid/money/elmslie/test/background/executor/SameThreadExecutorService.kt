package vivid.money.elmslie.test.background.executor

import java.util.concurrent.*

/**
 * An [ExecutorService] implementation for running code on the caller thread.
 * Source of inspiration: [stackoverflow comment](https://stackoverflow.com/a/13726463).
 */
class SameThreadExecutorService : AbstractExecutorService(), ScheduledExecutorService {

    @Volatile
    private var terminated = false

    override fun shutdown() = run { terminated = true }

    override fun isShutdown() = terminated

    override fun isTerminated() = terminated

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit) = shutdown().run { terminated }

    override fun shutdownNow() = emptyList<Runnable>()

    override fun execute(command: Runnable) = command.run()

    override fun schedule(
        command: Runnable,
        delay: Long,
        unit: TimeUnit
    ) = scheduleIsNotSupportedError()

    override fun <V : Any?> schedule(
        callable: Callable<V>,
        delay: Long,
        unit: TimeUnit
    ) = scheduleIsNotSupportedError()

    override fun scheduleAtFixedRate(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ) = scheduleIsNotSupportedError()

    override fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ) = scheduleIsNotSupportedError()

    private fun scheduleIsNotSupportedError(): Nothing = error("Schedule is not supported")
}
