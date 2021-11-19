package vivid.money.elmslie.core.store.binding

import vivid.money.elmslie.core.store.Store

/**
 * Creates a store that coordinates two stores together.
 *
 * Both stores are equal in terms of conversation possibilities.
 * For the period of the conversion definition the following naming is provided:
 * - Store which is specified as the receiver is called **initiator**
 * - Store which is coordinated is called **responder**.
 *
 * The resulting store will have the same interface as the **initiator** store.
 *
 * Example `Manager-Employee`:
 * ```
 * manager.coordinates(
 *     employee,
 *     //
 *     // Manager defines tasks without employee's awareness and dispatches:
 *     // - Up-to-date task definitions periodically
 *     // - News about promotion occasionally
 *     //
 *     // Employee chooses the way to handle tasks internally without manager's awareness.
 *     //
 *     dispatching = {
 *         states { EmployeeEvent.TaskDefinition(this) }
 *         effects { EmployeeEffect.PromotionNews }
 *     },
 *     //
 *     // Employee executes tasks without manager's awareness and updates manager with statuses.
 *     //
 *     // Manager processes task status updates without employee's awareness when:
 *     // - Receives up-to-date task status periodically
 *     // - And appreciates employee occasionally
 *     //
 *     receiving = {
 *         states { ManagerEvent.TaskStatus(this) }
 *         effects { ManagerEffect.EmployeeAppreciation }
 *     }
 * ).start()
 * ```
 *
 * It's possible to define the inverted conversation with another reversed coordination call.
 *
 * Example `One-on-one`:
 * ```
 * employee.coordinates(
 *     manager,
 *     //
 *     // Employee finds areas of improvement without manager's awareness and dispatches:
 *     // - Improvement suggestions periodically
 *     // - Satisfaction for the manager
 *     //
 *     // Manager chooses the time to process suggestions without employee's awareness.
 *     //
 *     dispatching = {
 *         states { ManagerEvent.ImprovementSuggestion(this) }
 *         effects { ManagerEvent.Satisfaction }
 *     },
 *     //
 *     // Manager refines suggestions without employee's awareness
 *     // and updates employee with possibilities.
 *     //
 *     // Employee processes possibilities and makes use of them when:
 *     // - Receives improvement possibilities
 *     // - And handles responsibility expansion
 *     //
 *     receiving = {
 *         states { EmployeeEvent.ImprovementPossibilities(this) }
 *         effects { EmployeeEvent.ResponsibilityExpansion }
 *     }
 * }.start()
 * ```
 *
 * @receiver - Initiates coordination
 * @param responder - Supports coordination
 * @param dispatching - Conversion contract implied by initiator
 * @param receiving - Conversion contract implied by responder
 */
fun <InitiatorEvent, InitiatorEffect, InitiatorState, ResponderEvent, ResponderEffect, ResponderState>
        Store<InitiatorEvent, InitiatorEffect, InitiatorState>.coordinates(
    responder: Store<ResponderEvent, ResponderEffect, ResponderState>,
    dispatching: ConversionContract<InitiatorEvent, InitiatorEffect, InitiatorState,
            ResponderEvent, ResponderEffect, ResponderState>.() -> Unit = {},
    receiving: ConversionContract<ResponderEvent, ResponderEffect, ResponderState,
            InitiatorEvent, InitiatorEffect, InitiatorState>.() -> Unit = {},
): Store<InitiatorEvent, InitiatorEffect, InitiatorState> =
    ConversationRules(this, responder, dispatching, receiving)
