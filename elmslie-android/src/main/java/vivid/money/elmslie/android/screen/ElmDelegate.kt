package vivid.money.elmslie.android.screen

import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

/**
 * Required part of ELM implementation for each fragment
 */
interface ElmDelegate<Event : Any, Effect : Any, State : Any> {

    val initEvent: Event

    /**
     * Examples:
     *
     * 1. Store that doesn't survive configuration change
     * ```
     * override fun createStore() = storeFactory.create()
     * ```
     *
     * 2. Store that survives configuration change
     * ```
     * override fun createStore() = storeFactory.create()
     *
     * override val storeHolder by retainStoreHolder { createStore() }
     * ```
     */
    val storeHolder: StoreHolder<Event, Effect, State>

    fun createStore(): Store<Event, Effect, State>?
    fun render(state: State)
    fun handleEffect(effect: Effect): Unit? = Unit

    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(state: State, list: List<Any>) {}
}
