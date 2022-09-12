package vivid.money.elmslie.core.store.binding

import vivid.money.elmslie.core.config.ElmslieConfig
import vivid.money.elmslie.core.store.Store

/**
 * A store that supervises both stores and manages their lifecycle.
 *
 * With the following responsibilities:
 * - [start] Starting both stores
 * - [stop] Stopping both stores
 *
 * @param initiator
 * - A store that demands data conversion
 * @param responder
 * - A store that handles data conversion
 * @param expecting A conversion contract that [initiator] dispatches for the [responder] to handle
 * @param receiving A conversion contract that [responder] provides to [initiator] in return
 * @constructor Determines conversion rules
 */
internal class ConversationRules<
    InitiatorEvent, InitiatorEffect, InitiatorState, ResponderEvent, ResponderEffect, ResponderState
>(
    private val initiator: Store<InitiatorEvent, InitiatorEffect, InitiatorState>,
    private val responder: Store<ResponderEvent, ResponderEffect, ResponderState>,
    expecting:
        ConversionContract<
            InitiatorEvent,
            InitiatorEffect,
            InitiatorState,
            ResponderEvent,
            ResponderEffect,
            ResponderState
        >.() -> Unit,
    receiving:
        ConversionContract<
            ResponderEvent,
            ResponderEffect,
            ResponderState,
            InitiatorEvent,
            InitiatorEffect,
            InitiatorState
        >.() -> Unit
) : Store<InitiatorEvent, InitiatorEffect, InitiatorState> by initiator {

    private val providedContract =
        ConversionContract(initiator, responder, ElmslieConfig.ioDispatchers).apply(expecting)
    private val expectedContract =
        ConversionContract(responder, initiator, ElmslieConfig.ioDispatchers).apply(receiving)

    override fun start(): Store<InitiatorEvent, InitiatorEffect, InitiatorState> {
        initiator.start()
        responder.start()
        providedContract.apply()
        expectedContract.apply()
        return this
    }

    override fun stop() {
        providedContract.revoke()
        expectedContract.revoke()
        responder.stop()
        initiator.stop()
    }
}
