package vivid.money.elmslie.samples.coroutines.timer.elm.timerstore

import vivid.money.elmslie.core.store.dsl_reducer.DslReducer

internal object TimerReducer : DslReducer<TimerEvent, TimerState, TimerEffect, TimerCommand>() {

    override fun Result.reduce(event: TimerEvent) = when (event) {
        is TimerEvent.Init -> {
            state { copy(isStarted = true) }
            commands { +TimerCommand.Start }
        }
        is TimerEvent.Start -> {
            state { copy(isStarted = true) }
            commands { +TimerCommand.Start }
        }
        is TimerEvent.Stop -> {
            state { copy(isStarted = false) }
            commands { +TimerCommand.Stop }
        }
        is TimerEvent.OnTimeTick -> {
            state { copy(secondsPassed = secondsPassed + 1) }
        }
        is TimerEvent.OnTimeError -> {
            state { copy(isStarted = false) }
            effects { +TimerEffect.Error(event.throwable) }
        }
    }
}
