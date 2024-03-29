package vivid.money.elmslie.rx2

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import vivid.money.elmslie.core.store.MappingActor

/**
 * Contains internal event mapping helpers for RxJava2
 */
@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActorCompat<Event : Any> : MappingActor<Event> {

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
        .onErrorResumeNext { t: Throwable ->
            Maybe.fromCallable { t.logErrorEvent(errorMapper) }.toObservable()
        }


    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(successEvent, { errorEvent })")
    )
    fun Completable.mapEvents(
        successEvent: Event,
        errorEvent: Event
    ): Observable<Event> = mapEvents(successEvent, { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(successEvent)")
    )
    fun Completable.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = mapEvents(successEvent)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = { errorEvent })")
    )
    fun Completable.mapErrorEvent(
        errorEvent: Event
    ): Observable<Event> = mapEvents(errorMapper = { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = errorMapper)")
    )
    fun Completable.mapErrorEvent(
        errorMapper: (Throwable) -> Event
    ): Observable<Event> = mapEvents(errorMapper = errorMapper)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents()")
    )
    fun Completable.ignoreEvents(): Observable<Event> = mapEvents()


    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents({ successEvent }, { failureEvent })")
    )
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ): Observable<Event> = mapEvents({ successEvent }, { failureEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents({ successEvent }, errorMapper)")
    )
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        errorMapper: (Throwable) -> Event
    ): Observable<Event> = mapEvents({ successEvent }, errorMapper)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper, { errorMapper })")
    )
    fun <T : Any> Single<T>.mapEvents(
        eventMapper: (T) -> Event?,
        errorEvent: Event
    ): Observable<Event> = mapEvents(eventMapper, { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents({ successEvent })")
    )
    fun <T : Any> Single<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = mapEvents({ successEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper)")
    )
    fun <T : Any> Single<T>.mapSuccessEvent(
        eventMapper: (T) -> Event
    ): Observable<Event> = mapEvents(eventMapper)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = { errorEvent })")
    )
    fun Single<Event>.mapErrorEvent(
        errorEvent: Event
    ): Observable<Event> = mapEvents(errorMapper = { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = errorMapper)")
    )
    fun Single<Event>.mapErrorEvent(
        errorMapper: (Throwable) -> Event
    ): Observable<Event> = mapEvents(errorMapper = errorMapper)


    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper, completionEvent = eventMapper(null)")
    )
    fun <T : Any> Maybe<T>.mapSuccessEvent(
        eventMapper: (T?) -> Event
    ): Observable<Event> = mapEvents(eventMapper, completionEvent = eventMapper(null))

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper, eventMapper(null)")
    )
    fun <T : Any> Maybe<T>.mapOnlySuccessEvent(
        eventMapper: (T) -> Event
    ): Observable<Event> = mapEvents(eventMapper)


    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper, { errorEvent }, completionEvent)")
    )
    fun <T : Any> Maybe<T>.mapEvents(
        eventMapper: (T) -> Event,
        completionEvent: Event,
        errorEvent: Event
    ): Observable<Event> = mapEvents(eventMapper, { errorEvent }, completionEvent)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(successEvent, errorMapper = { failureEvent )")
    )
    fun <T : Any> Observable<T>.mapEvents(
        successEvent: Event,
        errorEvent: Event
    ): Observable<Event> = mapEvents({ successEvent }, { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper, errorMapper = { errorEvent )")
    )
    fun <T : Any> Observable<T>.mapEvents(
        eventMapper: (T) -> Event,
        errorEvent: Event
    ): Observable<Event> = mapEvents(eventMapper, { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents({ successEvent })")
    )
    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEvent: Event
    ): Observable<Event> = mapEvents({ successEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(eventMapper)")
    )
    fun <T : Any> Observable<T>.mapSuccessEvent(
        eventMapper: (T) -> Event
    ): Observable<Event> = mapEvents(eventMapper)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = { errorEvent })")
    )
    fun Observable<Event>.mapErrorEvent(
        errorEvent: Event
    ): Observable<Event> = mapEvents(errorMapper = { errorEvent })

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents(errorMapper = errorMapper)")
    )
    fun Observable<Event>.mapErrorEvent(
        errorMapper: (Throwable) -> Event
    ): Observable<Event> = mapEvents(errorMapper = errorMapper)

    @Deprecated(
        "Please, use the default mapEvents method",
        ReplaceWith("mapEvents()")
    )
    fun <T : Any> Observable<T>.ignoreEvents(): Observable<Event> = mapEvents()
}
