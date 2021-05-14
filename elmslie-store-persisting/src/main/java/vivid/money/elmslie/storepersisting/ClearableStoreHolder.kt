package vivid.money.elmslie.storepersisting

import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

internal class ClearableStoreHolder<Event : Any, Effect : Any, State : Any>(
    storeProvider: () -> Store<Event, Effect, State>,
) : StoreHolder<Event, Effect, State> {

    override val store: Store<Event, Effect, State> = storeProvider().start()

    fun clear() {
        store.stop()
    }
}