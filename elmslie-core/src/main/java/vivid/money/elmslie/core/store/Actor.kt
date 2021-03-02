package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable

fun interface Actor<Command : Any, Event : Any> : MappingActor<Event> {

    /**
     * Executes a command.
     *
     * Contract for implementations:
     * - Implementations don't have to call subscribeOn
     * - By default subscription will be on the `io` scheduler
     */
    fun execute(command: Command): Observable<Event>
}
