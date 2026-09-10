package money.vivid.elmslie.android.logger

import money.vivid.elmslie.android.logger.strategy.AndroidLog
import money.vivid.elmslie.android.logger.strategy.Crash
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.logger.strategy.IgnoreLog

public fun ElmslieConfig.defaultReleaseLogger(): Unit = logger {
  fatal(Crash)
  nonfatal(IgnoreLog)
  debug(IgnoreLog)
}

public fun ElmslieConfig.defaultDebugLogger(): Unit = logger {
  fatal(Crash)
  nonfatal(AndroidLog.E)
  debug(AndroidLog.E)
}
