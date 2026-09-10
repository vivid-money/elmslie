package money.vivid.elmslie.core.logger

import money.vivid.elmslie.core.logger.strategy.LogStrategy

public class ElmslieLogConfiguration {

  private val strategies = mutableMapOf<LogSeverity, LogStrategy>()

  /** Report a certain bug in the client code */
  public fun fatal(strategy: LogStrategy): ElmslieLogConfiguration = apply {
    strategies[LogSeverity.Fatal] = strategy
  }

  /** Report an error in client code which can be identified as bug with certainty */
  public fun nonfatal(strategy: LogStrategy): ElmslieLogConfiguration = apply {
    strategies[LogSeverity.NonFatal] = strategy
  }

  /** Print informational message */
  public fun debug(strategy: LogStrategy): ElmslieLogConfiguration = apply {
    strategies[LogSeverity.Debug] = strategy
  }

  /** Apply the same logging strategy to all log levels */
  public fun always(strategy: LogStrategy): ElmslieLogConfiguration = apply {
    LogSeverity.entries.forEach { strategies[it] = strategy }
  }

  internal fun build() = ElmslieLogger(strategies)
}
