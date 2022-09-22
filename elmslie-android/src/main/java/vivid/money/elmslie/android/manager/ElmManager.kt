package vivid.money.elmslie.android.manager

import vivid.money.elmslie.core.store.Store

interface ElmManager<Event : Any, Effect : Any, State : Any> {
    val store: Store<Event, Effect, State>
}
