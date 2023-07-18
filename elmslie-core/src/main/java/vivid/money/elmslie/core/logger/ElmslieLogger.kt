package vivid.money.elmslie.core.logger

import vivid.money.elmslie.core.logger.strategy.IgnoreLog
import vivid.money.elmslie.core.logger.strategy.LogStrategy

/** Logs events happening in the Elmslie library */
class ElmslieLogger(
    private val strategy: Map<LogSeverity, LogStrategy>,
) {

    fun fatal(
        message: String = "",
        tag: String? = null,
        error: Throwable? = null,
    ) =
        handle(
            severity = LogSeverity.Fatal,
            message = message,
            tag = tag,
            error = error,
        )

    fun nonfatal(
        message: String = "",
        tag: String? = null,
        error: Throwable? = null,
    ) =
        handle(
            severity = LogSeverity.NonFatal,
            message,
            tag = tag,
            error = error,
        )

    fun debug(
        message: String,
        tag: String? = null,
    ) = handle(
        severity = LogSeverity.Debug,
        message,
        tag = tag,
        error = null,
    )

    private fun handle(severity: LogSeverity, message: String, tag: String?, error: Throwable?) {
        (strategy[severity] ?: IgnoreLog).log(
            severity = severity,
            message = message,
            tag = tag,
            throwable = error,
        )
    }
}
