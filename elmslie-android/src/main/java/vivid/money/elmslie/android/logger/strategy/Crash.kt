package vivid.money.elmslie.android.logger.strategy

import android.os.Handler
import android.os.Looper
import android.os.Message
import vivid.money.elmslie.core.logger.LogSeverity
import vivid.money.elmslie.core.logger.strategy.LogStrategy

/** Strategy that performs a crash on every log event it receives. Use wisely. */
object Crash : LogStrategy {

    private val errorHandler = Handler(Looper.getMainLooper()) { throw it.obj as Throwable }

    override fun invoke(severity: LogSeverity, message: String, error: Throwable?) {
        errorHandler.sendMessage(Message().also { it.obj = error ?: return })
    }
}