package vivid.money.elmslie.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface Store<Event, Effect, State> {

    /** Store's scope. Active for the lifetime of store. */
    val scope: CoroutineScope

    /** The current value of the [State]. The property is **thread-safe**. */
    val currentState: State

    /** Event that will be emitted upon store start. */
    val startEvent: Event?

    /**
     * Starts the operations inside the store. Throws **[StoreAlreadyStartedException]
     * [vivid.money.elmslie.core.store.exception.StoreAlreadyStartedException]** in case when the
     * store is already started.
     */
    fun start(): Store<Event, Effect, State>

    /** Stops all operations inside the store. */
    fun stop()

    /** Sends a new [Event] for the store. */
    fun accept(event: Event)

    /**
     * Returns the flow of [State]. Internally the store keeps the last emitted state value, so each
     * new subscribers will get it.
     *
     * Note that there will be no emission if a state isn't changed (it's [equals] method returned
     * `true`.
     *
     * By default, [State] is collected in [Dispatchers.IO].
     */
    fun states(): Flow<State>

    /**
     * Returns the flow of [Effect]. It's a _hot_ flow and values produced by it **don't cache**.
     *
     * In order to implement cache of [Effect], consider extending [Store] with appropriate
     * behavior.
     *
     * By default, [Effect] is collected in [Dispatchers.IO].
     */
    fun effects(): Flow<Effect>
}
