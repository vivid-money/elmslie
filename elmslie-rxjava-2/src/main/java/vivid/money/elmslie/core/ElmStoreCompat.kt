package vivid.money.elmslie.core

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import vivid.money.elmslie.core.store.StateReducer
import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.core.store.Store
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.store.DefaultActor

/**
 * A [Store] implementation that uses RxJava2 for multithreading
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

private fun <Command : Any, Event : Any> Actor<Command, Event>.toActor() =
    DefaultActor<Command, Event> { command, onEvent, onError ->
        val disposable = execute(command)
            .subscribeOn(Schedulers.io())
            .subscribe(
                onEvent,
                onError,
                { }
            )
        Disposable { disposable.dispose() }
    }

/**
 * An extension for accessing [Store] states as an [Observable]
 */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.states: Observable<State>
    get() = Observable.create { emitter ->
        val disposable = states(emitter::onNext)
        emitter.setCancellable { disposable.dispose() }
    }

/**
 * An extension for accessing [Store] effects as an [Observable]
 */
val <Event : Any, Effect : Any, State : Any> Store<Event, Effect, State>.effects: Observable<Effect>
    get() = Observable.create { emitter ->
        val disposable = effects(emitter::onNext)
        emitter.setCancellable { disposable.dispose() }
    }
