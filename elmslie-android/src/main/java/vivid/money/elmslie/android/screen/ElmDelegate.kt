package vivid.money.elmslie.android.screen

import vivid.money.elmslie.android.storeholder.ElmCreatorDelegate
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.android.storeholder.StoreHolder

interface ElmDelegate<Event : Any, Effect : Any, State : Any> :
    ElmRendererDelegate<Effect, State>, ElmCreatorDelegate<Event, Effect, State> {
    val initEvent: Event
    override val storeHolder: StoreHolder<Event, Effect, State>
}
