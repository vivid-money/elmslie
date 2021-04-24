package vivid.money.elmslie.android.screen

import vivid.money.elmslie.core.store.Store

/**
 * Required part of ELM implementation for each fragment
 */
interface ElmDelegate<Event : Any, Effect : Any, State : Any> {

    val initEvent: Event
    fun createStore(): Store<Event, Effect, State>
    fun render(state: State)
    fun handleEffect(effect: Effect) {}

    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(state: State, list: List<Any>) {}
}