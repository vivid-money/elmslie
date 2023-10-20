package vivid.money.elmslie.core.store.dsl

import vivid.money.elmslie.core.store.StateReducer
import kotlin.reflect.KClass

abstract class ScreenDslReducer<Event : Any, Ui : Any, Internal : Any, State : Any, Effect : Any, Command : Any>(
    private val uiEventClass: KClass<Ui>,
    private val internalEventClass: KClass<Internal>
) : StateReducer<Event, State, Effect, Command> {

    protected inner class Result(state: State) : ResultBuilder<State, Effect, Command>(state)

    protected abstract fun Result.ui(event: Ui): Any?

    protected abstract fun Result.internal(event: Internal): Any?

    final override fun reduce(
        event: Event,
        state: State
    ): vivid.money.elmslie.core.store.Result<State, Effect, Command> {
        val body = Result(state)
        @Suppress("UNCHECKED_CAST")
        when {
            uiEventClass.isInstance(event) -> body.ui(event as Ui)
            internalEventClass.isInstance(event) -> body.internal(event as Internal)
            else -> error("Event ${event::class} is neither UI nor Internal")
        }
        return body.build()
    }
}
