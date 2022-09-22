package vivid.money.elmslie.android.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store

abstract class ElmFragment<Event : Any, Effect : Any, State : Any> :
    Fragment, ElmDelegate<Event, Effect, State> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { requireActivity() }

    protected val store
        get() = storeHolder.store

    override val storeHolder: StoreHolder<Event, Effect, State> by fastLazy {
        createStoreHolder { createStore() }
    }

    override fun createStoreHolder(
        storeProvider: () -> Store<Event, Effect, State>
    ): StoreHolder<Event, Effect, State> =
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = storeProvider,
        )
}
