package vivid.money.elmslie.core.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Actor that doesn't emit any events after receiving a command */
class NoOpActor<Command : Any, Event : Any> : Actor<Command, Event>() {

    override fun execute(command: Command): Flow<Event> = emptyFlow()
}
