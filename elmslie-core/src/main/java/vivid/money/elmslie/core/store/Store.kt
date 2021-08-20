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

    fun <ChildEvent : Any, ChildState : Any, ChildEffect : Any> addChildStore(
        store: Store<ChildEvent, ChildEffect, ChildState>,
        eventMapper: (parentEvent: Event) -> ChildEvent? = { null },
        effectMapper: (parentState: State, childEffect: ChildEffect) -> Effect? = { _, _ -> null },
        stateReducer: (parentState: State, childState: ChildState) -> State = { parentState, _ -> parentState }
    ): Store<Event, Effect, State>
}