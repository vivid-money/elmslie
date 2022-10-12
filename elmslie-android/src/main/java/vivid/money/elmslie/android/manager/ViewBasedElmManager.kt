package vivid.money.elmslie.android.manager

import androidx.lifecycle.Lifecycle
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.core.store.Store

class ViewBasedElmManager<Event : Any, Effect : Any, State : Any>(
    storeHolderFactory: (() -> Store<Event, Effect, State>) -> StoreHolder<Event, Effect, State>,
    storeCreator: () -> Store<Event, Effect, State>,
    screenLifecycle: Lifecycle,
    initEventProvider: () -> Event,
) : ElmManager<Event, Effect, State> {

    override val store: Store<Event, Effect, State>
        get() = storeHolder.store

    private val storeHolder: StoreHolder<Event, Effect, State> =
        storeHolderFactory.invoke(storeCreator)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val storeStarter: ViewBasedStoreStarter<Event, Effect, State> =
        ViewBasedStoreStarter(
            storeHolder = storeHolder,
            screenLifecycle = screenLifecycle,
            initEventProvider = initEventProvider,
        )
}
