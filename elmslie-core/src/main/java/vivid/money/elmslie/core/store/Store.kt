package vivid.money.elmslie.core.store

import vivid.money.elmslie.core.disposable.Disposable

interface Store<Event, Effect, State> {

    /** Provides access to the current store [State]. */
    val currentState: State

    /** Returns `true` for the span duration between [start] and [stop] calls. */
    val isStarted: Boolean

    /**
     * Starts the operations inside the store.
     * Calls fatal exception handler in case the store is already started.
     */
    fun start(): Store<Event, Effect, State>

    /** Stops all operations inside the store. */
    fun stop()

    /** Sends a new [Event] for the store. */
    fun accept(event: Event)

    /**
     * Provides ability to subscribe to state changes.
     *
     * State dispatching is restricted. Behavior contract:
     * - The current state will be sent synchronously.
     * - Every two subsequent invocation of [onStateChange] have not equal states.
     * - States are **never** delivered on the main thread.
     *
     * @return [Disposable] For stopping [onStateChange] callback invocations.
     */
    fun states(onStateChange: (State) -> Unit): Disposable

    /**
     * Provides ability to subscribe to effect emissions.
     *
     * Effects may be buffered. Behavior contract:
     * - Buffering is active when the store [isStarted].
     * - Buffering starts after disposing all [effects] listeners.
     * - All buffered effects are sent to the first attached [effects] observer synchronously.
     * - Examples: Before the first [effects] call, after disposing [effects] observer.
     *
     * Emission thread is unspecified. Behavior contract:
     * - Effects are **never** delivered on the main thread.
     *
     * @return [Disposable] For stopping [onEffectEmission] callback invocations.
     */
    fun effects(onEffectEmission: (Effect) -> Unit): Disposable

    @Deprecated("Please, use store coordination instead. This approach will be removed in future.")
    fun <ChildEvent : Any, ChildState : Any, ChildEffect : Any> addChildStore(
        childStore: Store<ChildEvent, ChildEffect, ChildState>,
        eventMapper: (parentEvent: Event) -> ChildEvent? = { null },
        effectMapper: (parentState: State, childEffect: ChildEffect) -> Effect? = { _, _ -> null },
        stateReducer: (parentState: State, childState: ChildState) -> State = { parentState, _ -> parentState }
    ): Store<Event, Effect, State>
}
