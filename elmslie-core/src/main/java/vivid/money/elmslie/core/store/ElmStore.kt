package vivid.money.elmslie.core.store

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vivid.money.elmslie.core.ElmScope
import vivid.money.elmslie.core.config.ElmslieConfig

@Suppress("TooGenericExceptionCaught")
class ElmStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    private val reducer: StateReducer<Event, State, Effect, Command>,
    private val actor: Actor<Command, out Event>,
    storeListeners: Set<StoreListener<Event, State, Effect, Command>>? = null,
    override val startEvent: Event? = null,
) : Store<Event, Effect, State> {

    private val logger = ElmslieConfig.logger
    private val eventMutex = Mutex()

    private val effectsFlow = MutableSharedFlow<Effect>()

    private val statesFlow: MutableStateFlow<State> = MutableStateFlow(initialState)

    private val storeListeners: MutableSet<StoreListener<in Event, in State, in Effect, in Command>> =
        mutableSetOf<StoreListener<in Event, in State, in Effect, in Command>>().apply {
            ElmslieConfig.globalStoreListeners.forEach(::add)
            storeListeners?.forEach(::add)
        }

    override val scope = ElmScope("StoreScope")

    override val states: StateFlow<State> = statesFlow.asStateFlow()

    override val effects: Flow<Effect> = effectsFlow.asSharedFlow()

    override fun accept(event: Event) = dispatchEvent(event)

    override fun start(): Store<Event, Effect, State> {
        startEvent?.let(::accept)
        return this
    }

    override fun stop() {
        scope.cancel()
    }

    private fun dispatchEvent(event: Event) {
        scope.launch {
            try {
                storeListeners.forEach { it.onEvent(event) }
                logger.debug("New event: $event")
                val (_, effects, commands) =
                    eventMutex.withLock {
                        val oldState = statesFlow.value
                        val result = reducer.reduce(event, statesFlow.value)
                        val newState = result.state
                        statesFlow.value = newState
                        storeListeners.forEach { it.onStateChanged(newState, oldState, event) }
                        result
                    }
                effects.forEach { effect -> if (isActive) dispatchEffect(effect) }
                commands.forEach { if (isActive) executeCommand(it) }
            } catch (error: CancellationException) {
                throw error
            } catch (t: Throwable) {
                storeListeners.forEach { it.onReducerError(t, event) }
                logger.fatal("You must handle all errors inside reducer", t)
            }
        }
    }

    private suspend fun dispatchEffect(effect: Effect) {
        storeListeners.forEach { it.onEffect(effect) }
        logger.debug("New effect: $effect")
        effectsFlow.emit(effect)
    }

    private fun executeCommand(command: Command) {
        scope.launch {
            storeListeners.forEach { it.onCommand(command) }
            logger.debug("Executing command: $command")
            actor
                .execute(command)
                .cancellable()
                .catch { throwable ->
                    storeListeners.forEach { it.onCommandError(throwable, command) }
                    logger.nonfatal(error = throwable)
                }
                .collect { accept(it) }
        }
    }
}

fun <Event : Any, State : Any, Effect : Any> Store<Event, State, Effect>.toCachedStore() =
    EffectCachingElmStore(this)
