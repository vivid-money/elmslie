package vivid.money.elmslie.core.logger.strategy

import vivid.money.elmslie.core.logger.LogSeverity

/** Allows to provide custom logic for error handling */
fun interface LogStrategy : (LogSeverity, String, Throwable?) -> Unit
