package vivid.money.elmslie.core.store

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.exception.StoreAlreadyStartedException

@Suppress("TooManyFunctions", "TooGenericExceptionCaught")
class ElmStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    private val reducer: StateReducer<Event, State, Effect, Command>,
    private val actor: DefaultActor<Command, out Event>
) : Store<Event, Effect, State> {

    private val logger = ElmslieConfig.logger
    private val storeScope = CoroutineScope(ElmslieConfig.ioDispatchers + SupervisorJob())

    override val isStarted: Boolean
        get() = _isStarted.get()
    private val _isStarted = AtomicBoolean(false)

    private val effectsFlow = MutableSharedFlow<Effect>()

    override val currentState: State
        get() = statesFlow.value
    private val statesFlow: MutableStateFlow<State> = MutableStateFlow(initialState)

    override fun accept(event: Event) = dispatchEvent(event)

    override fun start() = this.also { requireNotStarted() }

    override fun stop() {
        _isStarted.set(false)
        storeScope.cancel()
    }

    override fun states(): Flow<State> = statesFlow.asStateFlow()

    override fun effects(): Flow<Effect> = effectsFlow.asSharedFlow()

    private fun dispatchEvent(event: Event) {
        storeScope.launch {
            try {
                logger.debug("New event: $event")
                val (state, effects, commands) = reducer.reduce(event, currentState)
                statesFlow.value = state
                effects.forEach(::dispatchEffect)
                commands.forEach(::executeCommand)
            } catch (t: Throwable) {
                logger.fatal("You must handle all errors inside reducer", t)
            }
        }
    }

    private fun dispatchEffect(effect: Effect) {
        storeScope.launch {
            logger.debug("New effect: $effect")
            effectsFlow.emit(effect)
        }
    }

    private fun executeCommand(command: Command) =
        try {
            logger.debug("Executing command: $command")
            storeScope.launch {
                actor
                    .execute(command)
                    .catch { logger.nonfatal(error = it) }
                    .collect { dispatchEvent(it) }
            }
        } catch (t: Throwable) {
            logger.fatal("Unexpected actor error", t)
        }

    private fun requireNotStarted() {
        if (!_isStarted.compareAndSet(false, true)) {
            logger.fatal("Store start error", StoreAlreadyStartedException())
        }
    }
}

fun <Event : Any, State : Any, Effect : Any, Command : Any> ElmStore<Event, State, Effect, Command>
    .toCachedStore() = ElmCachedStore(this)
