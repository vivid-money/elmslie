package vivid.money.elmslie.core.store.dsl_reducer

import vivid.money.elmslie.core.store.Result

open class ResultBuilder<State : Any, Effect : Any, Command : Any>(
    val initialState: State
) {

    private var currentState: State = initialState

    val state: State
        get() = currentState
    val commands = OperationsBuilder<Command>()
    val effects = OperationsBuilder<Effect>()

    fun update(update: State.() -> State) {
        currentState = currentState.update()
    }

    internal fun build() = Result(currentState, effects.build(), commands.build())
}