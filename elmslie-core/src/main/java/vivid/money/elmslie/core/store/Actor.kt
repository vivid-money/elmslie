package vivid.money.elmslie.core.store

import io.reactivex.Observable

fun interface Actor<Command : Any, Event : Any> : MappingActor<Event> {

    /**
     * Executes a command.
     *
     * Contract for implementations:
     * - Implementations must result an observable that doesn't emit errors
     * - Implementations don't have to call subscribeOn. By default subscription will be on the `io` schedulers
     */
    fun execute(command: Command): Observable<Event>
}

