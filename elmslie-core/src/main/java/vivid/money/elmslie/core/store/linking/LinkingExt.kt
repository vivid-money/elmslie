package vivid.money.elmslie.core.store.linking

import vivid.money.elmslie.core.store.Store

fun <
    ParentEvent : Any,
    ParentState : Any,
    ParentEffect : Any,
    ChildEvent : Any,
    ChildState : Any,
    ChildEffect : Any,
> Store<ParentEvent, ParentState, ParentEffect>.linkTo(
    childStore: Store<ChildEvent, ChildEffect, ChildState>,
    effectMapper: (ChildEffect) -> ParentEvent?,
    stateMapper: (ChildState) -> ParentEvent?,
): Store<ParentEvent, ParentState, ParentEffect> = apply {
    launch { childStore.states().collect { stateMapper.invoke(it)?.let(this@linkTo::accept) } }
    launch { childStore.effects().collect { effectMapper.invoke(it)?.let(this@linkTo::accept) } }
}

fun <
    ParentEvent : Any,
    ParentState : Any,
    ParentEffect : Any,
    ChildEvent : Any,
    ChildState : Any,
    ChildEffect : Any,
> Store<ParentEvent, ParentState, ParentEffect>.linkTo(
    childStore: Store<ChildEvent, ChildEffect, ChildState>,
    effectMapper: (ChildState, ChildEffect) -> ParentEvent? = { _, _ -> null },
    stateMapper: (ChildState, ChildState) -> ParentEvent? = { _, _ -> null },
): Store<ParentEvent, ParentState, ParentEffect> = apply {
    launch {
        var currentState = childStore.currentState
        childStore.states().collect {
            stateMapper.invoke(currentState, it)?.let(this@linkTo::accept)
            currentState = it
        }
        childStore.effects().collect {
            effectMapper.invoke(currentState, it)?.let(this@linkTo::accept)
        }
    }
}

fun <
    ParentEvent : Any,
    ParentState : Any,
    ParentEffect : Any,
    ChildEvent : Any,
    ChildState : Any,
    ChildEffect : Any,
> Store<ParentEvent, ParentState, ParentEffect>.linkListTo(
    childStore: Store<ChildEvent, ChildEffect, ChildState>,
    effectMapper: (ChildState, ChildEffect) -> List<ParentEvent> = { _, _ -> emptyList() },
    stateMapper: (ChildState, ChildState) -> List<ParentEvent> = { _, _ -> emptyList() },
): Store<ParentEvent, ParentState, ParentEffect> = apply {
    var currentState = childStore.currentState
    launch {
        childStore.states().collect {
            stateMapper.invoke(currentState, it).forEach(this@linkListTo::accept)
            currentState = it
        }
    }
    launch {
        childStore.effects().collect {
            effectMapper.invoke(currentState, it).forEach(this@linkListTo::accept)
        }
    }
}
