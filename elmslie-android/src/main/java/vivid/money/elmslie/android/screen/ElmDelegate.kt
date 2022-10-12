package vivid.money.elmslie.android.screen

import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.core.store.Store

interface ElmDelegate<Event : Any, Effect : Any, State : Any> : ElmRendererDelegate<Effect, State> {
    override val store: Store<Event, Effect, State>

    val initEvent: Event
}
