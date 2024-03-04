package money.vivid.elmslie.core.logger.strategy

import money.vivid.elmslie.core.logger.LogSeverity

/** Allows to provide custom logic for error handling */
fun interface LogStrategy {
    fun log(severity: LogSeverity, tag: String?, message: String, throwable: Throwable?)
}
