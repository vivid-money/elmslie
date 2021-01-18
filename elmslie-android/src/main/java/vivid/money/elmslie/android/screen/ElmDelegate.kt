package vivid.money.elmslie.android.screen

import vivid.money.elmslie.core.store.Store

/**
 * Required part of ELM implementation for each fragment
 */
interface ElmDelegate<Event : Any, Effect : Any, State : Any, MviStore : Store<Event, Effect, State>> {

    val initEvent: Event
    fun createStore(): MviStore
    fun render(state: State)
    fun handleEffect(effect: Effect) = Unit

    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(list: List<Any>) = Unit
}