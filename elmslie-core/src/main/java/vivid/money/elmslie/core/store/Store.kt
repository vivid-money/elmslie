package vivid.money.elmslie.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Store<Event : Any, Effect : Any, State : Any> {

    /** Event that will be emitted upon store start. */
    val startEvent: Event?

    /** Store's scope. Active for the lifetime of store. */
    val scope: CoroutineScope

    /**
     * Returns the flow of [State]. Internally the store keeps the last emitted state value, so each
     * new subscribers will get it.
     *
     * Note that there will be no emission if a state isn't changed (it's [equals] method returned
     * `true`.
     *
     * By default, [State] is collected in [Dispatchers.IO].
     */
    val states: StateFlow<State>

    /**
     * Returns the flow of [Effect]. It's a _hot_ flow and values produced by it **don't cache**.
     *
     * In order to implement cache of [Effect], consider extending [Store] with appropriate
     * behavior.
     *
     * By default, [Effect] is collected in [Dispatchers.IO].
     */
    val effects: Flow<Effect>

    /**
     * Starts the operations inside the store.
     */
    fun start(): Store<Event, Effect, State>

    /**
     * Stops all operations inside the store and cancels coroutines scope.
     * After this any calls of [start] method has no effect.
     */
    fun stop()

    /** Sends a new [Event] for the store. */
    fun accept(event: Event)
}
