package money.vivid.elmslie.android.logger.strategy

import android.util.Log
import money.vivid.elmslie.core.logger.strategy.LogStrategy

/** Uses default android logging mechanism for reporting */
public object AndroidLog {

  public val E: LogStrategy = log(Log::e)
  public val W: LogStrategy = log(Log::w)
  public val I: LogStrategy = log(Log::i)
  public val D: LogStrategy = log(Log::d)
  public val V: LogStrategy = log(Log::v)

  private fun log(log: (tag: String?, message: String?, throwable: Throwable?) -> Unit) =
    LogStrategy { _, tag, message, error ->
      log(tag, message, error)
    }
}
