package vivid.money.elmslie.samples.coroutines.timer.elm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import vivid.money.elmslie.core.ActorCompat
import vivid.money.elmslie.core.SwitcherCompat

internal object TimerActor : ActorCompat<Command, Event> {

    private val switcher = SwitcherCompat()

    override fun execute(command: Command) = when (command) {
        is Command.Start -> switcher.switch { secondsFlow() }
            .mapEvents({ Event.OnTimeTick }, Event::OnTimeError)
        is Command.Stop -> switcher.cancel()
            .ignoreEvents()
    }

    @Suppress("MagicNumber")
    private fun secondsFlow() = flow {
        generateSequence(0) { it + 1 }.forEach {
            delay(1000)
            emit(it)
        }
    }
}