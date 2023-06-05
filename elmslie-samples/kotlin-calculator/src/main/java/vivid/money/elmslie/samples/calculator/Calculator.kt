package vivid.money.elmslie.samples.calculator

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class Calculator {

    private val store = createStore()

    fun digit(digit: Char) = store.accept(Event.EnterDigit(digit))
    fun plus() = operation(Operation.PLUS)
    fun minus() = operation(Operation.MINUS)
    fun times() = operation(Operation.TIMES)
    fun divide() = operation(Operation.DIVIDE)

    private fun operation(operation: Operation) = store.accept(Event.PerformOperation(operation))

    fun evaluate() = store.accept(Event.Evaluate)

    fun errors(): Flow<Effect.NotifyError> = store.effects
        .filter { it is Effect.NotifyError }
        .map { it as Effect.NotifyError }

    fun results(): Flow<Effect.NotifyNewResult> = store.effects
        .filter { it is Effect.NotifyNewResult }
        .map { it as Effect.NotifyNewResult }
}
