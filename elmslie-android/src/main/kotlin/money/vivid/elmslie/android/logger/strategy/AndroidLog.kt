package money.vivid.elmslie.android.logger.strategy

import android.util.Log
import money.vivid.elmslie.core.logger.strategy.LogStrategy

/** Uses default android logging mechanism for reporting */
object AndroidLog {

    val E = log(Log::e)
    val W = log(Log::w)
    val I = log(Log::i)
    val D = log(Log::d)
    val V = log(Log::v)

    private fun log(
        log: (tag: String?, message: String?, throwable: Throwable?) -> Unit,
    ) = LogStrategy { _, tag, message, error -> log(tag, message, error) }
}
