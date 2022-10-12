package vivid.money.elmslie.android.renderer

import vivid.money.elmslie.core.store.Store

interface ElmRendererDelegate<Effect : Any, State : Any> {
    val store: Store<*, Effect, State>

    fun render(state: State)
    fun handleEffect(effect: Effect): Unit? = Unit
    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(state: State, list: List<Any>) {}
}
