package vivid.money.elmslie.android.logger.strategy

import android.util.Log
import vivid.money.elmslie.core.logger.LogSeverity
import vivid.money.elmslie.core.logger.strategy.LogStrategy

/** Uses default android logging mechanism for reporting */
object AndroidLog : LogStrategy by LogStrategy({ severity, message, error ->
    when (severity) {
        LogSeverity.Fatal -> Log.e(null, message, error)
        LogSeverity.NonFatal -> Log.w(null, message, error)
        LogSeverity.Debug -> Log.d(null, message, error)
    }
})