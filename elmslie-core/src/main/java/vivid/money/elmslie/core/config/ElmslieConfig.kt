package vivid.money.elmslie.core.config

import vivid.money.elmslie.core.logger.ElmslieLogger
import vivid.money.elmslie.core.logger.ElmslieLogConfiguration
import vivid.money.elmslie.core.logger.strategy.IgnoreLog

object ElmslieConfig {

    @Volatile
    private lateinit var loggerInternal: ElmslieLogger

    val logger: ElmslieLogger
        get() = loggerInternal

    init {
        logger { always(IgnoreLog) }
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
    fun logger(config: (ElmslieLogConfiguration.() -> Unit)): ElmslieLogger {
        return ElmslieLogConfiguration().apply(config).build().also { loggerInternal = it }
    }
}
