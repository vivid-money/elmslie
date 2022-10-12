package vivid.money.elmslie.core.config

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.switcher.Switcher

object ElmslieConfig {

    @Volatile private lateinit var _logger: ElmslieLogger

    @Volatile private lateinit var _reducerExecutor: ScheduledExecutorService

    @Volatile private lateinit var _ioDispatchers: CoroutineDispatcher

    @Volatile private var _shouldStopElmOnProcessDeath: Boolean = true

    val logger: ElmslieLogger
        get() = _logger

    val backgroundExecutor: ScheduledExecutorService
        get() = _reducerExecutor

    val ioDispatchers: CoroutineDispatcher
        get() = _ioDispatchers

    val shouldStopElmOnProcessDeath: Boolean
        get() = _shouldStopElmOnProcessDeath

    init {
        logger { always(IgnoreLog) }
        backgroundExecutor { Executors.newSingleThreadScheduledExecutor() }
        ioDispatchers { Dispatchers.IO }
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
        ElmslieLogConfiguration().apply(config).build().also { _logger = it }
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
        _reducerExecutor = builder()
    }

    /**
     * Configures CoroutineDispatcher for performing operations in background. Default is
     * [Dispatchers.IO]
     */
    fun ioDispatchers(builder: () -> CoroutineDispatcher) {
        _ioDispatchers = builder()
    }

    fun shouldStopElmOnProcessDeath(builder: () -> Boolean) {
        _shouldStopElmOnProcessDeath = builder()
    }
}
