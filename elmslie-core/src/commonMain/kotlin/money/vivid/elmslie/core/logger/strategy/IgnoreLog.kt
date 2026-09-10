package money.vivid.elmslie.core.logger.strategy

/** Ignores all log events */
public object IgnoreLog : LogStrategy by LogStrategy({ _, _, _, _ -> })
