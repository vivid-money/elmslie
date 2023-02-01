package vivid.money.elmslie.core.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import java.util.concurrent.Executors

object ElmslieConfig {

    @Volatile
    private lateinit var _logger: ElmslieLogger

    @Volatile
    private lateinit var _ioDispatchers: CoroutineDispatcher

    @Volatile
    private lateinit var _eventDispatchersFactory: () -> CoroutineDispatcher

    @Volatile
    private var _shouldStopOnProcessDeath: Boolean = true

    val logger: ElmslieLogger
        get() = _logger

    val ioDispatchers: CoroutineDispatcher
        get() = _ioDispatchers

    val eventDispatchers: CoroutineDispatcher
        get() = _eventDispatchersFactory.invoke()

    val shouldStopOnProcessDeath: Boolean
        get() = _shouldStopOnProcessDeath

    init {
        logger { always(IgnoreLog) }
        ioDispatchers { Dispatchers.IO }
        eventDispatchers { { Executors.newFixedThreadPool(1).asCoroutineDispatcher() } }
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
     * Configures CoroutineDispatcher for performing operations in background. Default is
     * [Dispatchers.IO]
     */
    fun ioDispatchers(builder: () -> CoroutineDispatcher) {
        _ioDispatchers = builder()
    }

    fun eventDispatchers(builder: () -> (() -> CoroutineDispatcher)) {
        _eventDispatchersFactory = builder()
    }

    fun shouldStopOnProcessDeath(builder: () -> Boolean) {
        _shouldStopOnProcessDeath = builder()
    }
}
