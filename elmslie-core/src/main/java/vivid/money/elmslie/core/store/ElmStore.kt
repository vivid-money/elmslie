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
    override val startEvent: Event? = null,
) : Store<Event, Effect, State> {

    private val logger = ElmslieConfig.logger
    private val eventMutex = Mutex()

    private val effectsFlow = MutableSharedFlow<Effect>()

    private val statesFlow: MutableStateFlow<State> = MutableStateFlow(initialState)

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
                logger.debug("New event: $event")
                val (_, effects, commands) =
                    eventMutex.withLock {
                        val result = reducer.reduce(event, statesFlow.value)
                        statesFlow.value = result.state
                        result
                    }
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

fun <Event : Any, State : Any, Effect : Any> Store<Event, State, Effect>.toCachedStore() =
    EffectCachingElmStore(this)
