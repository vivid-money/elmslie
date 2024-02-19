package money.vivid.elmslie.samples.calculator

import money.vivid.elmslie.core.store.ElmStore
import money.vivid.elmslie.core.store.NoOpActor
import money.vivid.elmslie.core.store.StateReducer

private const val MAX_INPUT_LENGTH = 9

val Reducer = object : StateReducer<Event, State, Effect, Command>() {
    override fun Result.reduce(event: Event) {
        when (event) {
            is Event.EnterDigit -> when {
                state.input.toString().length == MAX_INPUT_LENGTH -> {
                    effects { +Effect.NotifyError("Reached max input length") }
                }

                event.digit.isDigit() -> {
                    state { copy(input = "${state.input}${event.digit}".toInt()) }
                }

                else -> effects { +Effect.NotifyError("${event.digit} is not a digit") }
            }

            is Event.PerformOperation -> {
                val total = state.pendingOperation?.invoke(state.total, state.input) ?: state.total
                state { copy(pendingOperation = event.operation, total = total, input = 0) }
                effects { +Effect.NotifyNewResult(total) }
            }

            is Event.Evaluate -> {
                val total = state.pendingOperation?.invoke(state.total, state.input) ?: state.total
                state { copy(pendingOperation = null, total = total, input = 0) }
                effects { +Effect.NotifyNewResult(total) }
            }
        }
    }
}

fun createStore() = ElmStore(
    initialState = State(),
    reducer = Reducer,
    actor = NoOpActor()
).start()
