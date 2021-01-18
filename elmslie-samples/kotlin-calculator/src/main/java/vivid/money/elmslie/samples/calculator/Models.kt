package vivid.money.elmslie.samples.calculator

sealed class Event {
    data class EnterDigit(val digit: Char) : Event()
    data class PerformOperation(val operation: Operation) : Event()
    object Evaluate : Event()
}

sealed class Effect {
    data class NotifyError(val text: String) : Effect()
    data class NotifyNewResult(val result: Int) : Effect()
}

data class State(
    val pendingOperation: Operation? = Operation.PLUS, /* The first value should be added after entering */
    val total: Int = 0,
    val input: Int = 0
)

enum class Operation(
    op: (Int, Int) -> Int
) : (Int, Int) -> Int by op {

    TIMES(Int::times),
    PLUS(Int::plus),
    MINUS(Int::minus),
    DIVIDE(Int::div)
}
