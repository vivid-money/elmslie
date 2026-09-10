package money.vivid.elmslie.core.store.dsl

import money.vivid.elmslie.core.store.Result

public open class ResultBuilder<State : Any, Effect : Any, Command : Any>(
  public val initialState: State
) {

  private var currentState: State = initialState
  private val commandsBuilder = OperationsBuilder<Command>()
  private val effectsBuilder = OperationsBuilder<Effect>()

  public val state: State
    get() = currentState

  public fun state(update: State.() -> State) {
    currentState = currentState.update()
  }

  public fun commands(update: OperationsBuilder<Command>.() -> Unit) {
    commandsBuilder.update()
  }

  public fun effects(update: OperationsBuilder<Effect>.() -> Unit) {
    effectsBuilder.update()
  }

  internal fun build(): Result<State, Effect, Command> {
    return Result(currentState, effectsBuilder.build(), commandsBuilder.build())
  }
}
