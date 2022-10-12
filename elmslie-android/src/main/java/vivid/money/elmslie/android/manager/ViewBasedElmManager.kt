package vivid.money.elmslie.android.manager

import androidx.lifecycle.Lifecycle
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store

class ViewBasedElmManager<Event : Any, Effect : Any, State : Any>(
    storeProvider: () -> Store<Event, Effect, State>,
    screenLifecycle: Lifecycle,
    initEventProvider: () -> Event,
) : ElmManager<Event, Effect, State> {

    override val store: Store<Event, Effect, State> by fastLazy { storeProvider.invoke() }

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val storeStarter: ViewBasedStoreStarter<Event> =
        ViewBasedStoreStarter(
            storeProvider = { store },
            screenLifecycle = screenLifecycle,
            initEventProvider = initEventProvider,
        )
}
