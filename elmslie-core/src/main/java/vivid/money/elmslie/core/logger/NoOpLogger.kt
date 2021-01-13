package vivid.money.elmslie.core.logger

object NoOpLogger : ElmslieLogger {

    override fun fatal(message: String, error: Throwable?) = Unit

    override fun nonfatal(message: String, error: Throwable?) = Unit

    override fun error(message: String, error: Throwable?) = Unit

    override fun debug(message: String, error: Throwable?) = Unit
}