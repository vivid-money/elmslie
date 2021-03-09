package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable

interface Store<Event, Effect, State> {

    val currentState: State
    val states: Observable<State>
    val effects: Observable<Effect>

    fun accept(event: Event)

    val isStarted: Boolean
    fun start(): Store<Event, Effect, State>
    fun stop()
}