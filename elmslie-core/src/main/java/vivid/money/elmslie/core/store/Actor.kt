package vivid.money.elmslie.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import vivid.money.elmslie.core.config.ElmslieConfig

fun interface Actor<Command : Any, Event : Any> {

    /**
     * Executes a command. This method is performed on the [Dispatchers.IO]
     * [kotlinx.coroutines.Dispatchers.IO] which is set by ElmslieConfig.ioDispatchers()
     */
    fun execute(command: Command): Flow<Event>

    fun <T : Any> Flow<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (error: Throwable) -> Event? = { null }
    ) = mapNotNull { eventMapper(it) }
        .onEach { it.logSuccessEvent() }
        .catch { it.logErrorEvent(errorMapper)?.let { event -> emit(event) } ?: throw it }

    fun Throwable.logErrorEvent(errorMapper: (Throwable) -> Event?): Event? {
        return errorMapper(this).also {
            ElmslieConfig.logger.nonfatal(error = this)
            ElmslieConfig.logger.debug("Failed app state: $it")
        }
    }

    fun Event.logSuccessEvent() = ElmslieConfig.logger.debug("Completed app state: $this")
}
