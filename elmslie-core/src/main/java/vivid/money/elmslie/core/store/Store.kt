package vivid.money.elmslie.core.store

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

interface Store<Event, Effect, State> : Consumer<Event>, Disposable {

    val currentState: State
    val states: Observable<State>
    val effects: Observable<Effect>
    fun start(): Store<Event, Effect, State>
    fun stop()
}