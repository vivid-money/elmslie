package vivid.money.elmslie.samples.coroutines.timer.elm

import java.util.UUID
import vivid.money.elmslie.core.store.dsl.DslReducer

internal object TimerReducer : DslReducer<Event, State, Effect, Command>() {

    override fun Result.reduce(event: Event) =
        when (event) {
            is Event.Init -> {
                state {
                    copy(
                        isStarted = true,
                        generatedId =
                            if (state.generatedId == null) {
                                UUID.randomUUID().toString()
                            } else {
                                state.generatedId
                            },
                    )
                }
                commands { +Command.Start }
            }
            is Event.Start -> {
                state { copy(isStarted = true) }
                commands { +Command.Start }
            }
            is Event.Stop -> {
                state { copy(isStarted = false) }
                commands { +Command.Stop }
            }
            is Event.OnTimeTick -> {
                state { copy(secondsPassed = secondsPassed + 1) }
            }
            is Event.OnTimeError -> {
                state { copy(isStarted = false) }
                effects { +Effect.Error(event.throwable) }
            }
        }
}