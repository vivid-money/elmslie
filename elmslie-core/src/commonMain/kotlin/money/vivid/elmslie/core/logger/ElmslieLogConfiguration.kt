package money.vivid.elmslie.core.logger

import money.vivid.elmslie.core.logger.strategy.LogStrategy

class ElmslieLogConfiguration {

    private val strategies = mutableMapOf<LogSeverity, LogStrategy>()

    /** Report a certain bug in the client code */
    fun fatal(strategy: LogStrategy) = apply {
        strategies[LogSeverity.Fatal] = strategy
    }

    /** Report an error in client code which can be identified as bug with certainty */
    fun nonfatal(strategy: LogStrategy) = apply {
        strategies[LogSeverity.NonFatal] = strategy
    }

    /** Print informational message */
    fun debug(strategy: LogStrategy) = apply {
        strategies[LogSeverity.Debug] = strategy
    }

    /** Apply the same logging strategy to all log levels */
    fun always(strategy: LogStrategy) = apply {
        LogSeverity.entries.forEach { strategies[it] = strategy }
    }

    internal fun build() = ElmslieLogger(strategies)
}
