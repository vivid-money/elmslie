package vivid.money.elmslie.core.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.strategy.IgnoreLog

object ElmslieConfig {

    @Volatile private lateinit var _logger: ElmslieLogger

    @Volatile private lateinit var _ioDispatchers: CoroutineDispatcher

    val logger: ElmslieLogger
        get() = _logger

    val ioDispatchers: CoroutineDispatcher
        get() = _ioDispatchers

    init {
        logger { always(IgnoreLog) }
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
     * Configures CoroutineDispatcher for performing operations in background. Default is
     * [Dispatchers.IO]
     */
    fun ioDispatchers(builder: () -> CoroutineDispatcher) {
        _ioDispatchers = builder()
    }
}
