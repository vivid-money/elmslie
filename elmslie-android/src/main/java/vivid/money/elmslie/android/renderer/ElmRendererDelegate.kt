package vivid.money.elmslie.android.renderer

@Suppress("OptionalUnit")
interface ElmRendererDelegate<Effect : Any, State : Any> {
    fun render(state: State)
    fun handleEffect(effect: Effect): Unit? = Unit
    fun mapList(state: State): List<Any> = emptyList()
    fun renderList(state: State, list: List<Any>): Unit = Unit
}
