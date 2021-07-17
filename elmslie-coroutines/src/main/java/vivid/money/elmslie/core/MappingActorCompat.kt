package vivid.money.elmslie.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import vivid.money.elmslie.core.store.MappingActor

@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActorCompat<Event : Any> : MappingActor<Event> {

    fun <T : Any> Flow<T>.mapEvents(
        successEvent: Event,
        failureEvent: Event
    ) = asObservable().mapEvents(successEvent, failureEvent).asFlow()

    fun <T : Any> Flow<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEvent: Event
    ) = asObservable().mapEvents(successEventMapper, failureEvent).asFlow()

    fun <T : Any> Flow<T>.mapEvents(
        successEventMapper: (T) -> Event,
        failureEventMapper: (throwable: Throwable) -> Event
    ) = asObservable().mapEvents(successEventMapper, failureEventMapper).asFlow()

    fun <T : Any> Flow<T>.mapSuccessEvent(
        successEvent: Event
    ) = asObservable().mapSuccessEvent(successEvent).asFlow()

    fun <T : Any> Flow<T>.mapSuccessEvent(
        successEventMapper: (T) -> Event
    ) = asObservable().mapSuccessEvent(successEventMapper).asFlow()

    fun Flow<Event>.mapErrorEvent(
        failureEvent: Event
    ) = asObservable().mapErrorEvent(failureEvent).asFlow()

    fun Flow<Event>.mapErrorEvent(
        failureEvent: (Throwable) -> Event
    ) = asObservable().mapErrorEvent(failureEvent).asFlow()

    fun <T : Any> Flow<T>.ignoreEvents() = asObservable().ignoreEvents().asFlow()
}
