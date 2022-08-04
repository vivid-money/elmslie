package vivid.money.elmslie.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.store.DefaultActor
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.Store

/**
 * Compatibility [Store] implementation applying coroutines for multithreading.
 */
class ElmStoreCompat<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    reducer: StateReducer<Event, State, Effect, Command>,
    actor: Actor<Command, out Event>
) : Store<Event, Effect, State> by ElmStore(
    initialState = initialState,
    reducer = reducer,
    actor = actor.toActor()
)

@Suppress("TooGenericExceptionCaught", "RethrowCaughtException")
private fun <Command : Any, Event : Any> Actor<Command, Event>.toActor() =
    DefaultActor<Command, Event> { command, onEvent, onError ->
        val job = GlobalScope.launch(Dispatchers.Unconfined) {
            try {
                execute(command)
                    .flowOn(ElmslieConfig.backgroundExecutor.asCoroutineDispatcher())
                    .collect { event -> onEvent(event) }
            } catch (t: CancellationException) {
                throw t
            } catch (t: Throwable) {
                onError(t)
            }
        }
        Disposable { job.cancel() }
    }

/**
 * Extension for accessing [Store] states as a [Flow].
 */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.states: Flow<State>
    get() = callbackFlow {
        val disposable = states { state -> channel.trySend(state) }
        awaitClose(disposable::dispose)
    }

/**
 * Extension for accessing [Store] effects as a [Flow].
 */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.effects: Flow<Effect>
    get() = callbackFlow {
        val disposable = effects { effect -> channel.trySend(effect) }
        awaitClose(disposable::dispose)
    }
