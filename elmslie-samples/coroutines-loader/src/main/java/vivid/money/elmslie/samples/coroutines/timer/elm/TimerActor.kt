package vivid.money.elmslie.samples.coroutines.timer.elm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import vivid.money.elmslie.core.Actor
import vivid.money.elmslie.core.cancel
import vivid.money.elmslie.core.switch
import vivid.money.elmslie.core.switcher.Switcher

internal object TimerActor : Actor<Command, Event> {

    private val switcher = Switcher()

    override fun execute(command: Command) = when (command) {
        is Command.Start -> switcher.switch { secondsFlow() }
            .mapEvents({ Event.OnTimeTick }, Event::OnTimeError)
        is Command.Stop -> switcher.cancel()
            .mapEvents()
    }

    @Suppress("MagicNumber")
    private fun secondsFlow() = flow {
        repeat(10) {
            delay(1000)
            emit(0)
        }
        error("Test error")
    }
}
