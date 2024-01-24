package money.vivid.elmslie.android.logger

import money.vivid.elmslie.android.logger.strategy.AndroidLog
import money.vivid.elmslie.android.logger.strategy.Crash
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.logger.strategy.IgnoreLog

fun ElmslieConfig.defaultReleaseLogger() = logger {
    fatal(Crash)
    nonfatal(IgnoreLog)
    debug(IgnoreLog)
}

fun ElmslieConfig.defaultDebugLogger() = logger {
    fatal(Crash)
    nonfatal(AndroidLog.E)
    debug(AndroidLog.E)
}
