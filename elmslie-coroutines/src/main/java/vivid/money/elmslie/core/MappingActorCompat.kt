package vivid.money.elmslie.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import vivid.money.elmslie.core.store.MappingActor

/**
 * Contains internal event mapping helpers for coroutines
 */
@Suppress("ComplexInterface", "TooManyFunctions")
interface MappingActorCompat<Event : Any> : MappingActor<Event> {

    fun <T : Any> Flow<T>.mapEvents(
        eventMapper: (T) -> Event? = { null },
        errorMapper: (error: Throwable) -> Event? = { null }
    ) = mapNotNull { eventMapper(it) }
        .onEach { it.logSuccessEvent() }
        .catch { it.logErrorEvent(errorMapper)?.let { emit(it) } ?: throw it }

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents({ successEvent }, { errorEvent })")
    )
    fun <T : Any> Flow<T>.mapEvents(
        successEvent: Event,
        errorEvent: Event
    ) = mapEvents({ successEvent }, { errorEvent })

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents(eventMapper, { errorEvent })")
    )
    fun <T : Any> Flow<T>.mapEvents(
        eventMapper: (T) -> Event,
        errorEvent: Event
    ) = mapEvents(eventMapper, { errorEvent })

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents { successEvent }")
    )
    fun <T : Any> Flow<T>.mapSuccessEvent(
        successEvent: Event
    ) = mapEvents({ successEvent })

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents(eventMapper)")
    )
    fun <T : Any> Flow<T>.mapSuccessEvent(
        eventMapper: (T) -> Event
    ) = mapEvents(eventMapper)

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents(errorMapper = { errorEvent })")
    )
    fun <T : Any> Flow<T>.mapErrorEvent(
        errorEvent: Event
    ) = mapEvents(errorMapper = { errorEvent })

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents(errorMapper = errorMapper)")
    )
    fun Flow<Event>.mapErrorEvent(
        errorMapper: (Throwable) -> Event
    ) = mapEvents(errorMapper = errorMapper)

    @Deprecated(
        "Use mapEvents with mapping",
        ReplaceWith("this.mapEvents()")
    )
    fun <T : Any> Flow<T>.ignoreEvents() = mapEvents()
}
