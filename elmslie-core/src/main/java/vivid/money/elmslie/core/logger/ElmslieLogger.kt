package vivid.money.elmslie.core.logger

interface ElmslieLogger {

    fun fatal(message: String = "", error: Throwable? = null)
    fun nonfatal(message: String = "", error: Throwable? = null)
    fun error(message: String = "", error: Throwable? = null)
    fun debug(message: String = "", error: Throwable? = null)
}
