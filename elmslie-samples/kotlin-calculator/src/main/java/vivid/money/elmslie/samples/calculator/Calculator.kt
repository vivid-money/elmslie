package vivid.money.elmslie.samples.calculator

import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.rx3.effects

class Calculator {

    private val store = createStore()

    fun digit(digit: Char) = store.accept(Event.EnterDigit(digit))
    fun plus() = operation(Operation.PLUS)
    fun minus() = operation(Operation.MINUS)
    fun times() = operation(Operation.TIMES)
    fun divide() = operation(Operation.DIVIDE)

    private fun operation(operation: Operation) = store.accept(Event.PerformOperation(operation))

    fun evaluate() = store.accept(Event.Evaluate)

    fun errors(): Observable<Effect.NotifyError> = store.effects
        .filter { it is Effect.NotifyError }
        .map { it as Effect.NotifyError }

    fun results(): Observable<Effect.NotifyNewResult> = store.effects
        .filter { it is Effect.NotifyNewResult }
        .map { it as Effect.NotifyNewResult }
}
