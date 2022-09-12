package vivid.money.elmslie.core.store.binding

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import vivid.money.elmslie.core.store.Store

/** A contract for data exchange between stores. */
class ConversionContract<
    InitiatorEvent, InitiatorEffect, InitiatorState, ResponderEvent, ResponderEffect, ResponderState
>(
    private val initiator: Store<InitiatorEvent, InitiatorEffect, InitiatorState>,
    private val responder: Store<ResponderEvent, ResponderEffect, ResponderState>,
    ioDispatcher: CoroutineDispatcher,
) {

    private val coroutineScope: CoroutineScope = CoroutineScope(ioDispatcher)
    private val contracts = mutableSetOf<() -> Job>()
    private val contractsJobs = mutableSetOf<Job>()

    /** Defines full direct state conversion between stores. */
    fun states(conversion: InitiatorState.() -> ResponderEvent? = { null }) =
        states(cypher = { this }, conversion = conversion)

    /** Defines full direct effect conversion between stores. */
    fun effects(conversion: InitiatorEffect.() -> ResponderEvent? = { null }) =
        effects(cypher = { this }, conversion = conversion)

    /** Defines partial encrypted state conversion between stores. */
    fun <EncryptedState> states(
        cypher: InitiatorState.() -> EncryptedState?,
        conversion: EncryptedState.() -> ResponderEvent? = { null }
    ) = contract(valueProvider = initiator.states(), cypher = cypher, conversion = conversion)

    /** Defines partial encrypted effect conversion between stores. */
    fun <EncryptedEffect> effects(
        cypher: InitiatorEffect.() -> EncryptedEffect?,
        conversion: EncryptedEffect.() -> ResponderEvent? = { null }
    ) = contract(valueProvider = initiator.effects(), cypher = cypher, conversion = conversion)

    /**
     * Defines common conversion contract for data passing.
     *
     * Example:
     * ```
     * contract(store::states, cypher, conversion)
     * ```
     */
    private fun <Value, EncryptedValue> contract(
        valueProvider: Flow<Value>,
        cypher: Value.() -> EncryptedValue?,
        conversion: EncryptedValue.() -> ResponderEvent?
    ) {
        contracts += {
            coroutineScope.launch {
                valueProvider.collect { value ->
                    cypher(value)
                        ?.let { encrypted ->
                            conversion(encrypted)
                        }
                        ?.let {
                            responder.accept(it)
                        }
                }
            }
        }
    }

    /** Starts conversion between stores by applying contracts. */
    fun apply() {
        check(initiator.isStarted)
        check(responder.isStarted)
        contracts.forEach { contractsJobs += it.invoke() }

    }

    /** Stops conversion between stores by revoking contracts. */
    fun revoke() {
        contractsJobs.forEach { it.cancel() }
    }
}
