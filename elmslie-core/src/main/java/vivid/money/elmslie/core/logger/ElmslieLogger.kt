package vivid.money.elmslie.core.logger

import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import vivid.money.elmslie.core.logger.strategy.LogStrategy

/** Logs events happening in the Elmslie library */
class ElmslieLogger(
    private val strategy: Map<LogSeverity, LogStrategy>
) {

    fun fatal(message: String = "", error: Throwable? = null) = handle(LogSeverity.Fatal, message, error)

    fun nonfatal(message: String = "", error: Throwable? = null) = handle(LogSeverity.NonFatal, message, error)

    fun debug(message: String) = handle(LogSeverity.Debug, message, null)

    private fun handle(severity: LogSeverity, message: String, error: Throwable?) {
        (strategy[severity] ?: IgnoreLog)(severity, message, error)
    }
}
