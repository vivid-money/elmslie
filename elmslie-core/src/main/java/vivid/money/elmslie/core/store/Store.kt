package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

interface Store<Event, Effect, State> : Consumer<Event>, Disposable {

    val currentState: State
    val states: Observable<State>
    val effects: Observable<Effect>
    fun start(): Store<Event, Effect, State>
    fun stop()
}