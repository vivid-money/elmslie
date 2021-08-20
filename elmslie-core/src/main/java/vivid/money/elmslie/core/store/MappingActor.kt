package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.core.config.ElmslieConfig

@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActor<Event : Any> {

    fun Completable.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    fun Completable.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEventMapper)

    fun Completable.ignoreEvents(): Observable<Event> = toObservable()

    fun Completable.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = andThen(Observable.just(successEvent).mapSuccessEvent(successEvent))

    fun Completable.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = toObservable<Event>().mapErrorEvent(failureEvent)

    fun Completable.mapErrorEvent(
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = toObservable<Event>().mapErrorEvent(failureEventMapper)

    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEventMapper)

    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEvent)

    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEventMapper)

    fun <T : Any> Single<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = toObservable().mapSuccessEvent(successEvent)

    fun <T : Any> Single<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ): Observable<Event> = toObservable().mapSuccessEvent(successEventMapper)

    fun Single<Event>.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = toObservable().mapErrorEvent(failureEvent)

    fun Single<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ): Observable<Event> = toObservable().mapErrorEvent(failureEvent)

    fun <T> Maybe<T>.mapSuccessEvent(
        successEventMapper: (T?) -> Event
    ): Observable<Event> = map(::Option)
        .defaultIfEmpty(Option(null))
        .toObservable()
        .mapSuccessEvent { successEventMapper(it.value) }

    fun <T : Any> Maybe<T>.mapOnlySuccessEvent(
        successEventMapper: (T) -> Event
    ): Observable<Event> = toObservable()
        .mapSuccessEvent { successEventMapper(it) }

    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event
    ): Observable<Event> = map(successEventMapper)
        .defaultIfEmpty(completionEvent)
        .toObservable()

    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEvent: Event
    ): Observable<Event> = this
        .map(successEventMapper)
        .defaultIfEmpty(completionEvent)
        .mapErrorEvent(failureEvent)

    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ): Observable<Event> = this
        .map(successEventMapper)
        .defaultIfEmpty(completionEvent)
        .doOnSuccess { ElmslieConfig.logger.debug("Completed app state: $it") }
        .mapErrorEvent(failureEventMapper)

    fun <T : Any> Observable<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEvent)

    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEventMapper)

    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = mapSuccessEvent { successEvent }

    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ): Observable<Event> = map(successEventMapper).doOnNext {
        ElmslieConfig.logger.debug("Completed app state: $it")
    }

    fun Observable<Event>.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = mapErrorEvent { failureEvent }

    fun Observable<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ): Observable<Event> = onErrorReturn { error ->
        ElmslieConfig.logger.nonfatal(error = error)
        failureEvent(error).also { ElmslieConfig.logger.debug("Failed app state: $it") }
    }
}
