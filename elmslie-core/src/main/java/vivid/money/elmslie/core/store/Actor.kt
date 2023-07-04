package vivid.money.elmslie.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull

abstract class Actor<Command : Any, Event : Any> {

    /**
     * Executes a command. This method is performed on the [Dispatchers.IO]
     * [kotlinx.coroutines.Dispatchers.IO] which is set by ElmslieConfig.ioDispatchers()
     */
    abstract fun execute(command: Command): Flow<Event>

    fun <T : Any> Flow<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (error: Throwable) -> Event? = { null },
    ) = mapNotNull { eventMapper(it) }
        .catch { errorMapper(it) ?: throw it }
}
