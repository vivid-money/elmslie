package money.vivid.elmslie.core.logger.strategy

/** Ignores all log events */
object IgnoreLog : LogStrategy by LogStrategy({ _, _, _, _ -> })
