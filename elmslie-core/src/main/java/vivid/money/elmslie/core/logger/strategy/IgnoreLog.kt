package vivid.money.elmslie.core.logger.strategy

/** Ignores all log events */
object IgnoreLog : LogStrategy by LogStrategy({ _, _, _, _ -> })
