package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.core.config.ElmslieConfig

@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActor<Event : Any> {

    @JvmDefault
    fun Completable.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    @JvmDefault
    fun Completable.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun Completable.ignoreEvents(): Observable<Event> = toObservable()

    @JvmDefault
    fun Completable.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = andThen(Observable.just(successEvent).mapSuccessEvent(successEvent))

    @JvmDefault
    fun Completable.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = toObservable<Event>().mapErrorEvent(failureEvent)

    @JvmDefault
    fun Completable.mapErrorEvent(
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = toObservable<Event>().mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun <T : Any> Single<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = toObservable().mapSuccessEvent(successEvent)

    @JvmDefault
    fun <T : Any> Single<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ): Observable<Event> = toObservable().mapSuccessEvent(successEventMapper)

    @JvmDefault
    fun Single<Event>.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = toObservable().mapErrorEvent(failureEvent)

    @JvmDefault
    fun Single<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ): Observable<Event> = toObservable().mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T> Maybe<T>.mapSuccessEvent(
        successEventMapper: (T?) -> Event
    ): Observable<Event> = map(::Option)
        .defaultIfEmpty(Option(null))
        .toObservable()
        .mapSuccessEvent { successEventMapper(it.value) }

    @JvmDefault
    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEvent: Event
    ): Observable<Event> = this
        .map(successEventMapper)
        .defaultIfEmpty(completionEvent)
        .mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ): Observable<Event> = this
        .map(successEventMapper)
        .defaultIfEmpty(completionEvent)
        .doOnSuccess { ElmslieConfig.logger.debug("Completed app state: $it") }
        .mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEvent).mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEvent)

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ): Observable<Event> = mapSuccessEvent(successEventMapper).mapErrorEvent(failureEventMapper)

    @JvmDefault
    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = mapSuccessEvent { successEvent }

    @JvmDefault
    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ): Observable<Event> = map(successEventMapper).doOnNext {
        ElmslieConfig.logger.debug("Completed app state: $it")
    }

    @JvmDefault
    fun Observable<Event>.mapErrorEvent(
        failureEvent: Event
    ): Observable<Event> = mapErrorEvent { failureEvent }

    @JvmDefault
    fun Observable<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ): Observable<Event> = onErrorReturn { error ->
        ElmslieConfig.logger.nonfatal(error = error)
        failureEvent(error).also { ElmslieConfig.logger.debug("Failed app state: $it") }
    }
}