package vivid.money.elmslie.core.store

import vivid.money.elmslie.core.config.ElmslieConfig

/**
 * Contains internal event mapping utilities
 */
interface MappingActor<Event> {

    companion object {
        private val logger = ElmslieConfig.logger
    }

    fun Throwable.logErrorEvent(
        errorMapper: (Throwable) -> Event?
    ): Event? = errorMapper(this).also {
        logger.nonfatal(error = this)
        logger.debug("Failed app state: $it")
    }

    fun Event.logSuccessEvent() = logger.debug("Completed app state: $this")
}
