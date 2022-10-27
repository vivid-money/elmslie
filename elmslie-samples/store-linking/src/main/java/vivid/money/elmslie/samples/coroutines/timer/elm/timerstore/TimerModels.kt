package vivid.money.elmslie.samples.coroutines.timer.elm.timerstore

internal data class TimerState(
    val secondsPassed: Long = 0,
    val isStarted: Boolean = false
)

internal sealed class TimerEffect {
    data class Error(val throwable: Throwable) : TimerEffect()
}

internal sealed class TimerCommand {
    object Start : TimerCommand()
    object Stop : TimerCommand()
}

internal sealed class TimerEvent {
    object Init : TimerEvent()
    object Start : TimerEvent()
    object Stop : TimerEvent()
    object OnTimeTick : TimerEvent()
    data class OnTimeError(val throwable: Throwable) : TimerEvent()
}
