package vivid.money.elmslie.core.store

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import vivid.money.elmslie.core.config.ElmslieConfig

class ElmStore<Event : Any, State : Any, Effect : Any, Command : Any>(
    initialState: State,
    private val reducer: StateReducer<Event, State, Effect, Command>,
    private val actor: Actor<Command, Event>
) : Store<Event, Effect, State> {

    private val logger = ElmslieConfig.logger
    private val disposables = CompositeDisposable()
    private val scheduler = Schedulers.newThread() // TODO: Check if correct

    // We can't use subject to store state to keep it synchronized with children
    private val stateReferenceLock = Any()

    // Only to emit states to subscribers
    private val statesInternal = BehaviorSubject.createDefault(initialState)
    private val effectsInternal = PublishSubject.create<Effect>()
    private val effectsBuffer = EffectsBuffer(effectsInternal)
    private val eventsInternal = PublishSubject.create<Event>()
    private val commandsInternal = PublishSubject.create<Command>()

    override val effects: Observable<Effect> = effectsBuffer.getBufferedObservable()
    override val states: Observable<State> = statesInternal.distinctUntilChanged()
    override val currentState: State get() = statesInternal.value!!
    override fun accept(event: Event) = eventsInternal.onNext(event)

    override fun start(): Store<Event, Effect, State> {
        effectsBuffer.init().bind()

        eventsInternal
            .observeOn(scheduler)
            .flatMap { event ->
                val (effects, commands) = synchronized(stateReferenceLock) {
                    val state = statesInternal.value!!
                    val result = reducer.reduce(event, state)
                    statesInternal.onNext(result.state)
                    result.effects to result.commands
                }
                effects.forEach(effectsInternal::onNext)
                Observable.fromIterable(commands)
            }
            .subscribe(commandsInternal::onNext) {
                logger.fatal("You must handle all errors inside reducer", it)
            }
            .bind()

        commandsInternal
            .flatMap { command ->
                actor.execute(command)
                    .doOnError { logger.nonfatal(error = it) }
                    .onErrorResumeNext(Observable.empty())
                    .subscribeOn(io())
            }
            .subscribe(eventsInternal::onNext) {
                logger.error("Exception happened while executing a command", it)
            }
            .bind()

        return this
    }

    fun <ChildEvent : Any, ChildState : Any, ChildEffect : Any> addChildStore(
        store: Store<ChildEvent, ChildEffect, ChildState>,
        eventMapper: (parentEvent: Event) -> ChildEvent? = { null },
        effectMapper: (parentState: State, childEffect: ChildEffect) -> Effect? = { _, _ -> null },
        stateReducer: (parentState: State, childState: ChildState) -> State = { parentState, _ -> parentState }
    ): Store<Event, Effect, State> {
        eventsInternal
            .observeOn(scheduler)
            .flatMap { eventMapper(it)?.let { Observable.just(it) } ?: Observable.empty() }
            .subscribe(store::accept)
            .bind()

        // We won't lose any state or effects since thy're cached
        store.bind()
        store.start()

        store
            .effects
            .observeOn(scheduler)
            .flatMap { effect ->
                effectMapper(statesInternal.value!!, effect)?.let { Observable.just(it) } ?: Observable.empty()
            }
            .subscribe(effectsInternal::onNext)
            .bind()

        store
            .states
            .observeOn(scheduler)
            .map { childState ->
                synchronized(stateReferenceLock) {
                    val parentState = statesInternal.value!!
                    val newState = stateReducer(parentState, childState)
                    statesInternal.onNext(newState)
                }
            }
            .subscribe()
            .bind()

        return this
    }

    private fun Disposable.bind() = let(disposables::add)

    override fun dispose() = disposables.dispose()

    override fun isDisposed(): Boolean = disposables.isDisposed
}