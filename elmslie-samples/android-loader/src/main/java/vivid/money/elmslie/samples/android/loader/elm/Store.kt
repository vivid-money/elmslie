package vivid.money.elmslie.samples.android.loader.elm

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.core.store.*
import java.util.*
import java.util.concurrent.TimeUnit

class Actor : vivid.money.elmslie.core.store.Actor<Command, Event> {

    private val random = Random()

    override fun execute(command: Command): Observable<Event> = when (command) {
        is Command.LoadNewValue -> Single.timer(random.nextLong() % 2000 + 1000, TimeUnit.MILLISECONDS)
            .map { random.nextInt() }
            .doOnSuccess { if (it % 3 == 1) error("Simulate unexpected error") }
            .mapEvents(Event::ValueLoaded, Event.ErrorLoadingValue)
    }
}

class Reducer : StateReducer<Event, State, Effect, Command> {

    override fun reduce(event: Event, state: State): Result<State, Effect, Command> = when (event) {
        is Event.Init -> Result(state)
        is Event.ValueLoaded -> Result(state.copy(isLoading = false, currentValue = event.value))
        is Event.ErrorLoadingValue -> Result(state.copy(isLoading = false), effect = Effect.ShowError)
        is Event.ClickReload -> Result(
            state = state.copy(isLoading = true, currentValue = null),
            command = Command.LoadNewValue
        )
    }
}

fun storeFactory() = ElmStore(
    initialState = State(),
    reducer = Reducer(),
    actor = Actor()
).start()