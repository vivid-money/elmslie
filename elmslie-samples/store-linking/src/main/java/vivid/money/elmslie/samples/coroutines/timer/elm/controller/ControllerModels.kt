package vivid.money.elmslie.samples.coroutines.timer.elm.controller

internal data class ControllerState(
    val isStarted: Boolean = false
)

internal sealed class ControllerEffect

internal sealed class ControllerEvent {
    object Init : ControllerEvent()
    object Start : ControllerEvent()
    object Stop : ControllerEvent()
}

internal sealed class ControllerCommand
