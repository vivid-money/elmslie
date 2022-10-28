package vivid.money.elmslie.core.store

import kotlinx.coroutines.flow.Flow

fun interface DefaultActor<Command : Any, Event : Any> {

    /**
     * Executes a command. This method is performed on the [Dispatchers.IO]
     * [kotlinx.coroutines.Dispatchers.IO] which is set by ElmslieConfig.ioDispatchers()
     */
    fun execute(command: Command): Flow<Event>
}
