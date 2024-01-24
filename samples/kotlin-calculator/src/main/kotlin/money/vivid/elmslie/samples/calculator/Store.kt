package money.vivid.elmslie.samples.calculator

import money.vivid.elmslie.core.store.ElmStore
import money.vivid.elmslie.core.store.NoOpActor
import money.vivid.elmslie.core.store.StateReducer
import money.vivid.elmslie.core.store.Result


private const val MAX_INPUT_LENGTH = 9

val Reducer = StateReducer { event: Event, state: State ->
    when (event) {
        is Event.EnterDigit -> when {
            state.input.toString().length == MAX_INPUT_LENGTH -> {
                Result(state, effect = Effect.NotifyError("Reached max input length"))
            }
            event.digit.isDigit() -> {
                Result(state.copy(input = "${state.input}${event.digit}".toInt()))
            }
            else -> Result(state, effect = Effect.NotifyError("${event.digit} is not a digit"))
        }
        is Event.PerformOperation -> {
            val total = state.pendingOperation?.invoke(state.total, state.input) ?: state.total
            Result(
                state = state.copy(pendingOperation = event.operation, total = total, input = 0),
                effect = Effect.NotifyNewResult(total)
            )
        }
        is Event.Evaluate -> {
            val total = state.pendingOperation?.invoke(state.total, state.input) ?: state.total
            Result(
                state = state.copy(pendingOperation = null, total = total, input = 0),
                effect = Effect.NotifyNewResult(total)
            )
        }
    }
}

fun createStore() = ElmStore(
    initialState = State(),
    reducer = Reducer,
    actor = NoOpActor()
).start()
