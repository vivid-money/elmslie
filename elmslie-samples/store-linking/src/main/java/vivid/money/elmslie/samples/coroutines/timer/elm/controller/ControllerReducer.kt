package vivid.money.elmslie.samples.coroutines.timer.elm.controller

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal object ControllerReducer : DslReducer<
        ControllerEvent,
        ControllerState,
        ControllerEffect,
        ControllerCommand,
    >() {

    override fun Result.reduce(event: ControllerEvent) = when (event) {
        ControllerEvent.Init -> {}
        ControllerEvent.Start -> {
            state { copy(isStarted = true) }
        }
        ControllerEvent.Stop -> {
            state { copy(isStarted = false) }
        }
    }

}
