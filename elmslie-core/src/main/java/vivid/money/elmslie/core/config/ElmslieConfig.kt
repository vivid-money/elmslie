package vivid.money.elmslie.core.config

import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.switcher.Switcher
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object ElmslieConfig {

    @Volatile
    private lateinit var loggerInternal: ElmslieLogger

    @Volatile
    private lateinit var reducerExecutorInternal: ScheduledExecutorService

    val logger: ElmslieLogger
        get() = loggerInternal

    val backgroundExecutor: ScheduledExecutorService
        get() = reducerExecutorInternal

    init {
        logger { always(IgnoreLog) }
        backgroundExecutor { Executors.newSingleThreadScheduledExecutor() }
    }

    /**
     * Configures logging and error handling
     *
     * Example:
     * ```
     * ElmslieConfig.logger {
     *   fatal(Crash)
     *   nonfatal(AndroidLog)
     *   debug(Ignore)
     * }
     * ```
     */
    fun logger(config: (ElmslieLogConfiguration.() -> Unit)) {
        ElmslieLogConfiguration().apply(config).build().also { loggerInternal = it }
    }

    /**
     * Configures an executor for running background operations for [StateReducer] and [Switcher].
     *
     * Example:
     * ```
     * ElmslieConfig.backgroundExecutor { Executors.newScheduledThreadPool(4) }
     * ```
     */
    fun backgroundExecutor(builder: () -> ScheduledExecutorService) {
        reducerExecutorInternal = builder()
    }
}
