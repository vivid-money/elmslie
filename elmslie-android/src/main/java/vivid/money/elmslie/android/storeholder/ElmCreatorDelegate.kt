package vivid.money.elmslie.android.storeholder

import vivid.money.elmslie.core.store.Store

interface ElmCreatorDelegate<Event : Any, Effect : Any, State : Any> {
    fun createStore(): Store<Event, Effect, State>
    fun createStoreHolder(
        storeProvider: () -> Store<Event, Effect, State>
    ): StoreHolder<Event, Effect, State>
}