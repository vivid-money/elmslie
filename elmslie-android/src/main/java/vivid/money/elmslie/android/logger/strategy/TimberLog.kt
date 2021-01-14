package vivid.money.elmslie.android.logger.strategy

import timber.log.Timber
import vivid.money.elmslie.core.logger.strategy.LogStrategy

object TimberLog {

    val E = timberLogger(Timber::e)
    val W = timberLogger(Timber::w)
    val I = timberLogger(Timber::i)
    val D = timberLogger(Timber::d)
    val V = timberLogger(Timber::v)

    private fun timberLogger(
        log: (Throwable?, String) -> Unit
    ) = LogStrategy { _, message, error -> log(error, message) }
}

