package vivid.money.elmslie.rx3

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.core.config.ElmslieConfig

/**
 * Actor that supports event mappings for RxJava 3
 */
abstract class RxActor<Command : Any, Event : Any> {

    /**
     * Executes a command.
     *
     * Contract for implementations:
     * - Implementations don't have to call subscribeOn
     * - By default subscription will be on the `io` scheduler
     */
    abstract fun execute(command: Command): Observable<Event>

    fun Completable.mapEvents(
        completionEvent: Event? = null,
        errorMapper: (Throwable) -> Event? = { null }
    ): Observable<Event> = toObservable<Event>().mapEvents({ null }, errorMapper, completionEvent)

    fun <T : Any> Single<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (Throwable) -> Event? = { null },
        completionEvent: Event? = null,
    ): Observable<Event> = toObservable().mapEvents(eventMapper, errorMapper, completionEvent)

    fun <T : Any> Maybe<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (throwable: Throwable) -> Event? = { null },
        completionEvent: Event? = null,
    ): Observable<Event> = toObservable().mapEvents(eventMapper, errorMapper, completionEvent)

    fun <T : Any> Observable<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (throwable: Throwable) -> Event? = { null },
        completionEvent: Event? = null,
    ): Observable<Event> = flatMapMaybe { Maybe.fromCallable<Event> { eventMapper(it) } }
        .switchIfEmpty(Maybe.fromCallable<Event> { completionEvent }.toObservable())
        .doOnNext { it.logSuccessEvent() }
        .onErrorResumeNext { Observable.fromIterable(listOfNotNull(it.logErrorEvent(errorMapper))) }

    private fun Throwable.logErrorEvent(errorMapper: (Throwable) -> Event?): Event? {
        return errorMapper(this).also {
            ElmslieConfig.logger.nonfatal(error = this)
            ElmslieConfig.logger.debug("Failed app state: $it")
        }
    }

    private fun Event.logSuccessEvent() = ElmslieConfig.logger.debug("Completed app state: $this")
}
