package vivid.money.elmslie.core.store

import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import vivid.money.elmslie.core.ElmBackgroundScope
import vivid.money.elmslie.core.ElmEventScope
import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.exception.StoreAlreadyStartedException

@Suppress("TooGenericExceptionCaught")
class ElmStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    private val reducer: StateReducer<Event, State, Effect, Command>,
    private val actor: DefaultActor<Command, out Event>,
    override val startEvent: Event? = null,
) : Store<Event, Effect, State> {

    private val logger = ElmslieConfig.logger
    private val eventScope = ElmEventScope("EventScope")

    override val isStarted: Boolean
        get() = _isStarted.get()
    private val _isStarted = AtomicBoolean(false)

    private val effectsFlow = MutableSharedFlow<Effect>()

    override val currentState: State
        get() = statesFlow.value
    private val statesFlow: MutableStateFlow<State> = MutableStateFlow(initialState)

    override val scope = ElmBackgroundScope("CommandScope")

    override fun accept(event: Event) = dispatchEvent(event)

    override fun start(): Store<Event, Effect, State> {
        if (_isStarted.compareAndSet(false, true)) {
            startEvent?.let(::accept)
        } else {
            logger.fatal("Store start error", StoreAlreadyStartedException())
        }
        return this
    }

    override fun stop() {
        _isStarted.set(false)
        scope.cancel()
    }

    override fun states(): Flow<State> = statesFlow.asStateFlow()

    override fun effects(): Flow<Effect> = effectsFlow.asSharedFlow()

    private fun dispatchEvent(event: Event) {
        eventScope.launch {
            try {
                logger.debug("New event: $event")
                val (state, effects, commands) = reducer.reduce(event, currentState)
                statesFlow.value = state
                effects.forEach { effect -> if (isActive) dispatchEffect(effect) }
                commands.forEach { if (isActive) executeCommand(it) }
            } catch (error: CancellationException) {
                throw error
            } catch (t: Throwable) {
                logger.fatal("You must handle all errors inside reducer", t)
            }
        }
    }

    private suspend fun dispatchEffect(effect: Effect) {
        logger.debug("New effect: $effect")
        effectsFlow.emit(effect)
    }

    private fun executeCommand(command: Command) {
        scope.launch {
            logger.debug("Executing command: $command")
            actor
                .execute(command)
                .cancellable()
                .catch { logger.nonfatal(error = it) }
                .collect { dispatchEvent(it) }
        }
    }
}

fun <Event : Any, State : Any, Effect : Any> Store<Event, State, Effect>
    .toCachedStore() = EffectCachingElmStore(this)
