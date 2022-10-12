package vivid.money.elmslie.android.screen

import androidx.lifecycle.*
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter

class ElmScreen<Event : Any, Effect : Any, State : Any>(
    private val delegate: ElmDelegate<Event, Effect, State>,
    screenLifecycle: Lifecycle,
) {

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elmRenderer =
        ElmRenderer(
            delegate = delegate,
            screenLifecycle = screenLifecycle,
        )

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elmStarter =
        ViewBasedStoreStarter(
            storeProvider = { delegate.store },
            screenLifecycle = screenLifecycle,
            initEventProvider = { delegate.initEvent },
        )
}
