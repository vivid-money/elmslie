package vivid.money.elmslie.core.store.binding

import vivid.money.elmslie.core.disposable.CompositeDisposable
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.store.Store

/**
 * A contract for data exchange between stores.
 */
class ConversionContract<InitiatorEvent, InitiatorEffect, InitiatorState,
        ResponderEvent, ResponderEffect, ResponderState>(
    private val initiator: Store<InitiatorEvent, InitiatorEffect, InitiatorState>,
    private val responder: Store<ResponderEvent, ResponderEffect, ResponderState>,
) {

    private val disposable = CompositeDisposable()
    private val contracts = mutableSetOf<() -> Disposable>()

    /**
     * Defines full direct state conversion between stores.
     */
    fun states(
        conversion: InitiatorState.() -> ResponderEvent? = { null }
    ) = states({ this }, conversion)

    /**
     * Defines full direct effect conversion between stores.
     */
    fun effects(
        conversion: InitiatorEffect.() -> ResponderEvent? = { null }
    ) = effects({ this }, conversion)

    /**
     * Defines partial encrypted state conversion between stores.
     */
    fun <EncryptedState> states(
        cypher: InitiatorState.() -> EncryptedState?,
        conversion: EncryptedState.() -> ResponderEvent? = { null }
    ) = contract(initiator::states, cypher, conversion)

    /**
     * Defines partial encrypted effect conversion between stores.
     */
    fun <EncryptedEffect> effects(
        cypher: InitiatorEffect.() -> EncryptedEffect?,
        conversion: EncryptedEffect.() -> ResponderEvent? = { null }
    ) = contract(initiator::effects, cypher, conversion)

    /**
     * Defines common conversion contract for data passing.
     *
     * Example:
     * ```
     * contract(store::states, cypher, conversion)
     * ```
     */
    private fun <Value, EncryptedValue> contract(
        valueProvider: ((Value) -> Unit) -> Disposable,
        cypher: Value.() -> EncryptedValue?,
        conversion: EncryptedValue.() -> ResponderEvent?
    ) {
        contracts += {
            valueProvider { value ->
                cypher(value)
                    ?.let { encrypted -> conversion(encrypted) }
                    ?.let(responder::accept)
            }
        }
    }

    /**
     * Starts conversion between stores by applying contracts.
     */
    fun apply() {
        check(initiator.isStarted)
        check(responder.isStarted)
        contracts.forEach { it() }
    }

    /**
     * Stops conversion between stores by revoking contracts.
     */
    fun revoke() {
        disposable.clear()
    }
}
