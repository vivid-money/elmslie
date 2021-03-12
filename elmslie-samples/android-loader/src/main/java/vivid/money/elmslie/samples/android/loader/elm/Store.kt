package vivid.money.elmslie.samples.android.loader.elm

import io.reactivex.Observable
import vivid.money.elmslie.core.ActorCompat
import vivid.money.elmslie.core.ElmStoreCompat
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer
import vivid.money.elmslie.samples.android.loader.elm.Event.Internal
import vivid.money.elmslie.samples.android.loader.elm.Event.Ui
import vivid.money.elmslie.samples.android.loader.repository.ValueRepository

class Actor : ActorCompat<Command, Internal> {

    override fun execute(command: Command): Observable<Internal> = when (command) {
        is Command.LoadNewValue -> ValueRepository.getValue()
            .mapEvents(Internal::ValueLoaded, Internal.ErrorLoadingValue)
    }
}

class Reducer : ScreenDslReducer<Event, Ui, Internal, State, Effect, Command>(
    Ui::class, Internal::class) {

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ValueLoaded -> {
            state { copy(isLoading = false, value = event.value) }
        }
        is Internal.ErrorLoadingValue -> {
            state { copy(isLoading = false) }
            effects { +Effect.ShowError }
        }
    }

    override fun Result.ui(event: Ui) = when (event) {
        is Ui.Init -> {
            state { copy(isLoading = true) }
            commands { +Command.LoadNewValue }
        }
        is Ui.ClickReload -> {
            state { copy(isLoading = true, value = null) }
            commands { +Command.LoadNewValue }
        }
    }
}

fun storeFactory() = ElmStoreCompat(
    initialState = State(),
    reducer = Reducer(),
    actor = Actor()
).start()