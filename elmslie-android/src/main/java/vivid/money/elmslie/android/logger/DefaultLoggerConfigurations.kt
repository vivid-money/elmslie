package vivid.money.elmslie.android.logger

import vivid.money.elmslie.android.logger.strategy.Crash
import vivid.money.elmslie.android.logger.strategy.AndroidLog
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.logger.strategy.IgnoreLog

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
