package vivid.money.elmslie.core.store.binding

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import vivid.money.elmslie.core.store.Store

/**
 * A contract for data exchange between stores
 */
class ConversionContract<InitiatorEvent, InitiatorEffect, InitiatorState,
        ResponderEvent, ResponderEffect, ResponderState>(
    private val initiator: Store<InitiatorEvent, InitiatorEffect, InitiatorState>,
    private val responder: Store<ResponderEvent, ResponderEffect, ResponderState>,
) {

    private val disposable = CompositeDisposable()
    private val contracts = mutableSetOf<() -> Unit>()

    /**
     * Defines full state conversion between stores
     */
    fun states(
        conversion: InitiatorState.() -> ResponderEvent? = { null }
    ) = states({ this }, conversion)

    /**
     * Defines full effect conversion between stores
     */
    fun effects(
        conversion: InitiatorEffect.() -> ResponderEvent? = { null }
    ) = effects({ this }, conversion)

    /**
     * Defines encrypted state conversion between stores
     */
    fun <EncryptedState> states(
        cypher: Observable<InitiatorState>.() -> Observable<EncryptedState>,
        conversion: EncryptedState.() -> ResponderEvent? = { null }
    ) {
        initiator.states.cypher() to responder with conversion
    }

    /**
     * Defines encrypted effect conversion between stores
     */
    fun <EncryptedEffect> effects(
        cypher: Observable<InitiatorEffect>.() -> Observable<EncryptedEffect>,
        conversion: EncryptedEffect.() -> ResponderEvent? = { null }
    ) {
        initiator.effects.cypher() to responder with conversion
    }

    /**
     * Defines conversion before passing to another store
     *
     * Sample: `manager.states to employee using conversion`
     */
    private infix fun <T, Result> Pair<Observable<T>, Store<Result, *, *>>.with(
        conversion: (T) -> Result?
    ) {
        contracts += {
            first
                .flatMapMaybe { Maybe.fromCallable<Result> { conversion(it) } }
                .subscribe(second::accept)
                .let(disposable::add)
        }
    }

    /**
     * Starts the conversion between stores
     */
    fun apply() {
        check(initiator.isStarted)
        check(responder.isStarted)
        contracts.forEach { it() }
    }

    /**
     * Stops the conversion between stores
     */
    fun revoke() {
        disposable.clear()
    }
}
