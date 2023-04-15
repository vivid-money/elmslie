package vivid.money.elmslie.samples.coroutines.timer.elm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.coroutines.Actor
import vivid.money.elmslie.coroutines.cancel
import vivid.money.elmslie.coroutines.switch

internal object TimerActor : Actor<Command, Event> {

    private val switcher = Switcher()

    override fun execute(command: Command) =
        when (command) {
            is Command.Start ->
                switcher
                    .switch { secondsFlow() }
                    .mapEvents(
                        eventMapper = { Event.OnTimeTick },
                        errorMapper = { Event.OnTimeError(it) },
                    )
            is Command.Stop -> switcher.cancel().mapEvents()
        }

    @Suppress("MagicNumber")
    private fun secondsFlow() = flow {
        repeat(10) {
            delay(1000)
            emit(it)
        }
        error("Test error")
    }
}
