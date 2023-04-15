package vivid.money.elmslie.samples.coroutines.timer.elm

internal data class State(
    val id: String,
    val secondsPassed: Long = 0,
    val isStarted: Boolean = false
)

internal sealed class Effect {
    data class Error(val throwable: Throwable) : Effect()
}

internal sealed class Command {
    object Start : Command()
    object Stop : Command()
}

internal sealed class Event {
    object Init : Event()
    object Start : Event()
    object Stop : Event()
    object OnTimeTick : Event()
    data class OnTimeError(val throwable: Throwable) : Event()
}
