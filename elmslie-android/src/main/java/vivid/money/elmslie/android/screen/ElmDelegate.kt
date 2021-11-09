package vivid.money.elmslie.android.screen

import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

/**
 * Required part of ELM implementation for each fragment
 */
interface ElmDelegate<Event : Any, Effect : Any, State : Any> {

    val initEvent: Event
    val storeHolder: StoreHolder<Event, Effect, State>

    @Deprecated("Use storeHolder property instead")
    fun createStore(): Store<Event, Effect, State>? = null
    fun render(state: State)
    fun handleEffect(effect: Effect): Unit? = Unit

    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(state: State, list: List<Any>) {}
}
