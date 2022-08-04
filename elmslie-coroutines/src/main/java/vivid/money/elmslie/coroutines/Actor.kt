package vivid.money.elmslie.coroutines

import kotlinx.coroutines.flow.Flow

/**
 * Actor that supports event mappings for coroutines
 */
fun interface Actor<Command : Any, Event : Any> : MappingActorCompat<Event> {

    /**
     * Executes a command.
     *
     * Contract for implementations:
     * - Implementations don't have to call subscribeOn
     * - By default subscription will be on the `io` scheduler
     */
    fun execute(command: Command): Flow<Event>
}
