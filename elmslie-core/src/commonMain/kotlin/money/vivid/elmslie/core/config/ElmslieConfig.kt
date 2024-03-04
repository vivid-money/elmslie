package money.vivid.elmslie.core.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import money.vivid.elmslie.core.logger.ElmslieLogConfiguration
import money.vivid.elmslie.core.logger.ElmslieLogger
import money.vivid.elmslie.core.logger.strategy.IgnoreLog
import money.vivid.elmslie.core.store.StoreListener
import kotlin.concurrent.Volatile

object ElmslieConfig {

    @Volatile
    private lateinit var _logger: ElmslieLogger

    @Volatile private lateinit var _ioDispatchers: CoroutineDispatcher

    @Volatile private var _shouldStopOnProcessDeath: Boolean = true

    @Volatile private var _globalStoreListeners: Set<StoreListener<Any, Any, Any, Any>> = emptySet()

    val logger: ElmslieLogger
        get() = _logger

    val ioDispatchers: CoroutineDispatcher
        get() = _ioDispatchers

    val shouldStopOnProcessDeath: Boolean
        get() = _shouldStopOnProcessDeath

    val globalStoreListeners: Set<StoreListener<Any, Any, Any, Any>>
        get() = _globalStoreListeners

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

    fun shouldStopOnProcessDeath(builder: () -> Boolean) {
        _shouldStopOnProcessDeath = builder()
    }

    fun globalStoreListeners(builder: () -> Set<StoreListener<Any, Any, Any, Any>>) {
        _globalStoreListeners = builder()
    }
}
