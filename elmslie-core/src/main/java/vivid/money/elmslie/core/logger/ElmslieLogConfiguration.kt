package vivid.money.elmslie.core.logger

import vivid.money.elmslie.core.logger.strategy.LogStrategy

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
        LogSeverity.values().forEach { strategies[it] = strategy }
    }

    internal fun build() = ElmslieLogger(strategies)
}
