package vivid.money.elmslie.core.config

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.switcher.Switcher

object ElmslieConfig {

    @Volatile private lateinit var loggerInternal: ElmslieLogger

    @Volatile private lateinit var reducerExecutorInternal: ScheduledExecutorService

    @Volatile private lateinit var _ioDispatchers: CoroutineDispatcher

    val logger: ElmslieLogger
        get() = loggerInternal

    val backgroundExecutor: ScheduledExecutorService
        get() = reducerExecutorInternal

    val ioDispatchers: CoroutineDispatcher
        get() = _ioDispatchers

    val coroutineScope: CoroutineScope

    init {
        logger { always(IgnoreLog) }
        backgroundExecutor { Executors.newSingleThreadScheduledExecutor() }
        ioDispatchers { Dispatchers.IO }
        coroutineScope = CoroutineScope(ioDispatchers + SupervisorJob())
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

    /**
     * Configures CoroutineDispatcher for performing operations in background. Default is
     * [Dispatchers.IO]
     */
    fun ioDispatchers(builder: () -> CoroutineDispatcher) {
        _ioDispatchers = builder()
    }
}
