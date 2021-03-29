package vivid.money.elmslie.core

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import vivid.money.elmslie.core.store.MappingActor

@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActorCompat<Event : Any> : MappingActor<Event> {

    @JvmDefault
    fun Completable.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEvent, failureEvent).toV2()

    @JvmDefault
    fun Completable.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ) = toV3().mapEvents(successEvent, failureEventMapper).toV2()

    @JvmDefault
    fun Completable.ignoreEvents() = toV3().ignoreEvents().toV2()

    @JvmDefault
    fun Completable.mapSuccessEvent(
        successEvent: Event
    ) = toV3().mapSuccessEvent(successEvent).toV2()

    @JvmDefault
    fun Completable.mapErrorEvent(
        failureEvent: Event
    ) = toV3().mapErrorEvent(failureEvent).toV2()

    @JvmDefault
    fun Completable.mapErrorEvent(
        failureEventMapper: (Throwable) -> Event
    ) = toV3().mapErrorEvent(failureEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEvent, failureEvent).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEvent: Event,
        failureEventMapper: (Throwable) -> Event
    ) = toV3().mapEvents(successEvent, failureEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEventMapper, failureEvent).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (Throwable) -> Event
    ) = toV3().mapEvents(successEventMapper, failureEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapSuccessEvent(
        successEvent: Event
    ) = toV3().mapSuccessEvent(successEvent).toV2()

    @JvmDefault
    fun <T : Any> Single<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ) = toV3().mapSuccessEvent(successEventMapper).toV2()

    @JvmDefault
    fun Single<Event>.mapErrorEvent(
        failureEvent: Event
    ) = toV3().mapErrorEvent(failureEvent).toV2()

    @JvmDefault
    fun Single<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ) = toV3().mapErrorEvent(failureEvent).toV2()

    @JvmDefault
    fun <T> Maybe<T>.mapSuccessEvent(
        successEventMapper: (T?) -> Event
    ) = toV3().mapSuccessEvent(successEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEventMapper, completionEvent, failureEvent).toV2()

    @JvmDefault
    fun <T : Any> Maybe<T>.mapEvents(
        successEventMapper: (T) -> Event,
        completionEvent: Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ) = toV3().mapEvents(successEventMapper, completionEvent, failureEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEvent, failureEvent).toV2()

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ) = toV3().mapEvents(successEventMapper, failureEvent).toV2()

    @JvmDefault
    fun <T : Any> Observable<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ) = toV3().mapEvents(successEventMapper, failureEventMapper).toV2()

    @JvmDefault
    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEvent: Event
    ) = toV3().mapSuccessEvent(successEvent).toV2()

    @JvmDefault
    fun <T : Any> Observable<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ) = toV3().mapSuccessEvent(successEventMapper).toV2()

    @JvmDefault
    fun Observable<Event>.mapErrorEvent(
        failureEvent: Event
    ) = toV3().mapErrorEvent(failureEvent).toV2()

    @JvmDefault
    fun Observable<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ) = toV3().mapErrorEvent(failureEvent).toV2()
}