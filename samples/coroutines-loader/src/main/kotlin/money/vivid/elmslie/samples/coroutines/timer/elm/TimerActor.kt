package money.vivid.elmslie.samples.coroutines.timer.elm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import money.vivid.elmslie.core.store.Actor
import money.vivid.elmslie.core.switcher.Switcher

internal object TimerActor : Actor<Command, Event>() {

    private val switcher = Switcher()

    override fun execute(command: Command) =
        when (command) {
            is Command.Start -> secondsFlow()
                .switchOnEach(command)
                .mapEvents(
                    eventMapper = { Event.OnTimeTick },
                    errorMapper = { Event.OnTimeError(it) },
                )

            is Command.Stop -> switchers.getOrPut(Command.Start::class) {
                Switcher()
            }.cancel()
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
