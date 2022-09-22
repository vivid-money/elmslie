package vivid.money.elmslie.core.store.linking

import vivid.money.elmslie.core.store.Store

fun <
    ConsumerEvent : Any,
    ConsumerState : Any,
    ConsumerEffect : Any,
    ProducerEvent : Any,
    ProducerState : Any,
    ProducerEffect : Any,
> Store<ConsumerEvent, ConsumerState, ConsumerEffect>.linkTo(
    producerStore: Store<ProducerEvent, ProducerEffect, ProducerState>,
    effectMapper: (ProducerEffect) -> ConsumerEvent?,
    stateMapper: (ProducerState) -> ConsumerEvent?,
): Store<ConsumerEvent, ConsumerState, ConsumerEffect> = apply {
    launch {
        producerStore.states().collect { stateMapper.invoke(it)?.let(this@linkTo::accept) }
    }
    launch {
        producerStore.effects().collect { effectMapper.invoke(it)?.let(this@linkTo::accept) }
    }
}

fun <
    ConsumerEvent : Any,
    ConsumerState : Any,
    ConsumerEffect : Any,
    ProducerEvent : Any,
    ProducerState : Any,
    ProducerEffect : Any,
> Store<ConsumerEvent, ConsumerState, ConsumerEffect>.linkTo(
    producerStore: Store<ProducerEvent, ProducerEffect, ProducerState>,
    effectMapper: (ProducerState, ProducerEffect) -> ConsumerEvent? = { _, _ -> null },
    stateMapper: (ProducerState, ProducerState) -> ConsumerEvent? = { _, _ -> null },
): Store<ConsumerEvent, ConsumerState, ConsumerEffect>  = apply {
    launch {
        producerStore.states().collect {
            stateMapper.invoke(producerStore.currentState, it)?.let(this@linkTo::accept)
        }
    }
    launch {
        producerStore.effects().collect {
            effectMapper.invoke(producerStore.currentState, it)?.let(this@linkTo::accept)
        }
    }
}

fun <
    ConsumerEvent : Any,
    ConsumerState : Any,
    ConsumerEffect : Any,
    ProducerEvent : Any,
    ProducerState : Any,
    ProducerEffect : Any,
> Store<ConsumerEvent, ConsumerState, ConsumerEffect>.linkListTo(
    producerStore: Store<ProducerEvent, ProducerEffect, ProducerState>,
    effectMapper: (ProducerState, ProducerEffect) -> List<ConsumerEvent> = { _, _ -> emptyList() },
    stateMapper: (ProducerState, ProducerState) -> List<ConsumerEvent> = { _, _ -> emptyList() },
): Store<ConsumerEvent, ConsumerState, ConsumerEffect>  = apply {
    launch {
        producerStore.states().collect {
            stateMapper.invoke(producerStore.currentState, it).forEach(this@linkListTo::accept)
        }
    }
    launch {
        producerStore.effects().collect {
            effectMapper.invoke(producerStore.currentState, it).forEach(this@linkListTo::accept)
        }
    }
}
