package vivid.money.elmslie.android.screen

import androidx.lifecycle.Lifecycle
import vivid.money.elmslie.core.store.Store

/**
 * Required part of MVI implementation for each fragment
 */
interface MviDelegate<Event : Any, Effect : Any, State : Any, MviStore : Store<Event, Effect, State>> {

    val screenLifecycle: Lifecycle

    val initEvent: Event
    fun createStore(): MviStore
    fun render(state: State)
    fun handleEffect(effect: Effect) = Unit

    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(list: List<Any>) = Unit
}