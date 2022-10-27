package vivid.money.elmslie.samples.coroutines.timer.elm.timerstore

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.coroutines.Actor
import vivid.money.elmslie.coroutines.cancel
import vivid.money.elmslie.coroutines.switch

internal class TimerActor(private val errorTimeout: Int = 10) : Actor<TimerCommand, TimerEvent> {

    private val switcher = Switcher()

    override fun execute(command: TimerCommand) =
        when (command) {
            is TimerCommand.Start ->
                switcher
                    .switch { secondsFlow() }
                    .mapEvents({ TimerEvent.OnTimeTick }, TimerEvent::OnTimeError)
            is TimerCommand.Stop -> switcher.cancel().mapEvents()
        }

    @Suppress("MagicNumber")
    private fun secondsFlow() = flow {
        repeat(errorTimeout) {
            delay(1000)
            emit(0)
        }
        error("Test error")
    }
}
