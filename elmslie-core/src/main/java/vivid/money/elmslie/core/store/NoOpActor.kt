package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable

/**
 * Actor that doesn't emit any events after receiving a command
 */
class NoOpActor<Command : Any, Event : Any> : Actor<Command, Event> {

    override fun execute(command: Command): Observable<Event> = Observable.never()
}