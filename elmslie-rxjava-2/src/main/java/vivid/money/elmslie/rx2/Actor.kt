package vivid.money.elmslie.rx2

import io.reactivex.Observable

/**
 * Actor that supports event mappings for RxJava 2
 */
fun interface Actor<Command : Any, Event : Any> : MappingActorCompat<Event> {

    /**
     * Executes a command.
     *
     * Contract for implementations:
     * - Implementations don't have to call subscribeOn
     * - By default subscription will be on the `io` scheduler
     */
    fun execute(command: Command): Observable<Event>
}
