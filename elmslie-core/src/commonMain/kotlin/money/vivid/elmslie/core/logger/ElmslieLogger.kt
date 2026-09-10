package money.vivid.elmslie.core.logger

import money.vivid.elmslie.core.logger.strategy.IgnoreLog
import money.vivid.elmslie.core.logger.strategy.LogStrategy

/** Logs events happening in the Elmslie library */
public class ElmslieLogger(private val strategy: Map<LogSeverity, LogStrategy>) {

  public fun fatal(message: String = "", tag: String? = null, error: Throwable? = null): Unit =
    handle(severity = LogSeverity.Fatal, message = message, tag = tag, error = error)

  public fun nonfatal(message: String = "", tag: String? = null, error: Throwable? = null): Unit =
    handle(severity = LogSeverity.NonFatal, message, tag = tag, error = error)

  public fun debug(message: String, tag: String? = null): Unit =
    handle(severity = LogSeverity.Debug, message, tag = tag, error = null)

  private fun handle(severity: LogSeverity, message: String, tag: String?, error: Throwable?) {
    (strategy[severity] ?: IgnoreLog).log(
      severity = severity,
      message = message,
      tag = tag,
      throwable = error,
    )
  }
}
