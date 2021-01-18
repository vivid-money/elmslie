package vivid.money.elmslie.samples.calculator

class Calculator {

    private val store = createStore()

    fun digit(digit: Char) = store.accept(Event.EnterDigit(digit))
    fun plus() = operation(Operation.PLUS)
    fun minus() = operation(Operation.MINUS)
    fun times() = operation(Operation.TIMES)
    fun divide() = operation(Operation.DIVIDE)

    private fun operation(operation: Operation) = store.accept(Event.PerformOperation(operation))

    fun evaluate() = store.accept(Event.Evaluate)
    fun errors() = store.effects.filter { it is Effect.NotifyError }.map { it as Effect.NotifyError }
    fun results() = store.effects.filter { it is Effect.NotifyNewResult }.map { it as Effect.NotifyNewResult }
}