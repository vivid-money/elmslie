package vivid.money.elmslie.core.store

import kotlinx.coroutines.CancellationException
import vivid.money.elmslie.core.config.ElmslieConfig

/** Contains internal event mapping utilities */
interface MappingActor<Event> {

    companion object {
        private val logger = ElmslieConfig.logger
    }

    fun Throwable.logErrorEvent(errorMapper: (Throwable) -> Event?): Event? {
        val error = (this as? CancellationException)?.cause ?: this
        return errorMapper(error).also {
            logger.nonfatal(error = error)
            logger.debug("Failed app state: $it")
        }
    }

    fun Event.logSuccessEvent() = logger.debug("Completed app state: $this")
}
