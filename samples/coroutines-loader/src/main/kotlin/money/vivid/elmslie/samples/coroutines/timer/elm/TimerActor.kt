package money.vivid.elmslie.samples.coroutines.timer.elm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor

internal object TimerActor : Actor<Command, Event>() {

  override fun execute(command: Command) =
    when (command) {
      is Command.Start ->
        secondsFlow()
          .switch(Command.Start::class)
          .mapEvents(eventMapper = { Event.OnTimeTick }, errorMapper = { Event.OnTimeError(it) })

      is Command.Stop -> cancelSwitchFlowOn<Command.Start>().mapEvents()
    }

  @Suppress("MagicNumber")
  private fun secondsFlow(): Flow<Int> = flow {
    repeat(10) {
      delay(1000)
      emit(it)
    }
    error("Test error")
  }
}

internal inline fun <reified StartCommand : Any> Actor<*, *>.cancelSwitchFlowOn(): Flow<Unit> {
  return cancelSwitchFlows(StartCommand::class)
}
