package vivid.money.elmslie.core.store

import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.util.distinctUntilChanged
import vivid.money.elmslie.core.store.exception.StoreAlreadyStartedException
import vivid.money.elmslie.core.disposable.CompositeDisposable
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.util.ConcurrentHashSet
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@Suppress("TooManyFunctions", "TooGenericExceptionCaught")
class ElmStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    private val reducer: StateReducer<Event, State, Effect, Command>,
    private val actor: DefaultActor<Command, out Event>
) : Store<Event, Effect, State> {

    companion object {
        private val logger = ElmslieConfig.logger
        private val executor = ElmslieConfig.backgroundExecutor
    }

    private val disposables = CompositeDisposable()

    private val isStartedInternal = AtomicBoolean(false)
    override val isStarted: Boolean get() = isStartedInternal.get()

    private val effectBuffer = ConcurrentLinkedQueue<Effect>()
    private val effectBufferingListener = effectBuffer::add
    private val effectListeners = ConcurrentHashSet<(Effect) -> Any?>()
    private val eventListeners = ConcurrentHashSet<(Event) -> Any?>()
    private val stateListeners = ConcurrentHashSet<(State) -> Any?>()
    private val stateInternal = AtomicReference(initialState)
    override val currentState: State get() = stateInternal.get()

    // We can't use subject to store state to keep it synchronized with children
    private val stateLock = Any()

    override fun accept(event: Event) = dispatchEvent(event)

    override fun start() = this.also {
        requireNotStarted()
        stopBuffering()
    }

    override fun stop() {
        isStartedInternal.set(false)
        disposables.clear()
        startBuffering()
    }

    override fun states(onStateChange: (State) -> Unit): Disposable {
        val callback = onStateChange.distinctUntilChanged()
        stateListeners += callback
        dispatchState(currentState)
        return Disposable { stateListeners -= callback }
    }

    override fun effects(onEffectEmission: (Effect) -> Unit): Disposable {
        dispatchBuffer(onEffectEmission)
        startBuffering()
        effectListeners += onEffectEmission
        return Disposable {
            effectListeners -= onEffectEmission
            if (isStarted && effectListeners.isEmpty()) stopBuffering()
        }
    }

    private fun events(onEventTriggering: (Event) -> Unit): Disposable {
        eventListeners += onEventTriggering
        return Disposable { eventListeners -= onEventTriggering }
    }

    override fun <ChildEvent : Any, ChildState : Any, ChildEffect : Any> addChildStore(
        childStore: Store<ChildEvent, ChildEffect, ChildState>,
        eventMapper: (parentEvent: Event) -> ChildEvent?,
        effectMapper: (parentState: State, childEffect: ChildEffect) -> Effect?,
        stateReducer: (parentState: State, childState: ChildState) -> State
    ): Store<Event, Effect, State> {
        disposables.addAll(
            // We won't lose any state or effects since they're cached
            { childStore.stop() },
            events { eventMapper(it)?.let(childStore::accept) },
            childStore.effects { effectMapper(currentState, it)?.let(::dispatchEffect) },
            childStore.states { dispatchState(stateReducer(currentState, it)) },
        )
        childStore.start()
        return this
    }

    private fun dispatchState(state: State) = synchronized(stateLock) {
        stateInternal.set(state)
        stateListeners.forEach { it(state) }
    }

    private fun dispatchEffect(effect: Effect) {
        logger.debug("New effect: $effect")
        effectListeners.forEach { it(effect) }
    }

    private fun dispatchEvent(event: Event) {
        executor.submit {
            try {
                logger.debug("New event: $event")
                val result = reducer.reduce(event, currentState)
                dispatchState(result.state)
                result.effects.forEach(::dispatchEffect)
                result.commands.forEach(::executeCommand)
            } catch (t: Throwable) {
                logger.fatal("You must handle all errors inside reducer", t)
            }
        }
    }

    private fun executeCommand(command: Command) = try {
        logger.debug("Executing command: $command")
        disposables += actor.execute(command, ::dispatchEvent, { logger.nonfatal(error = it) })
    } catch (t: Throwable) {
        logger.fatal("Unexpected actor error", t)
    }

    private fun startBuffering() = effectListeners.add(effectBufferingListener)

    private fun stopBuffering() = effectListeners.remove(effectBufferingListener)

    private fun dispatchBuffer(onEffectEmission: (Effect) -> Unit) = effectBuffer
        .onEach { onEffectEmission(it) }
        .clear()

    private fun requireNotStarted() {
        if (!isStartedInternal.compareAndSet(false, true)) {
            logger.fatal("Store start error", StoreAlreadyStartedException())
        }
    }
}
