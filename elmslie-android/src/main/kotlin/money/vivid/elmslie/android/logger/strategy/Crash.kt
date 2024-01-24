package money.vivid.elmslie.android.logger.strategy

import android.os.Handler
import android.os.Looper
import android.os.Message
import money.vivid.elmslie.core.logger.LogSeverity
import money.vivid.elmslie.core.logger.strategy.LogStrategy

/** Strategy that performs a crash on every log event it receives. Use wisely. */
object Crash : LogStrategy {

    private val errorHandler = Handler(Looper.getMainLooper()) { throw it.obj as Throwable }

    override fun log(severity: LogSeverity, tag: String?, message: String, throwable: Throwable?) {
        errorHandler.sendMessage(
            Message().apply {
                obj = throwable ?: Exception(message)
            }
        )
    }
}
