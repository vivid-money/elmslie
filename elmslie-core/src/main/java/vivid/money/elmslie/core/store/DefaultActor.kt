package vivid.money.elmslie.core.store

import kotlinx.coroutines.flow.Flow

fun interface DefaultActor<Command : Any, Event : Any> {

    /**
     * Executes a command. This method is always called in ElmslieConfig.backgroundExecutor. Usually
     * background thread.
     */
    fun execute(command: Command): Flow<Event>
}
