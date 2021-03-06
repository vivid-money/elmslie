package vivid.money.elmslie.android.logger.strategy

import android.util.Log
import vivid.money.elmslie.core.logger.strategy.LogStrategy

/** Uses default android logging mechanism for reporting */
object AndroidLog {

    val E = log(Log::e)
    val W = log(Log::w)
    val I = log(Log::i)
    val D = log(Log::d)
    val V = log(Log::v)

    private fun log(
        log: (String?, String?, Throwable?) -> Unit
    ) = LogStrategy { _, message, error -> log(null, message, error) }
}
