package vivid.money.elmslie.compose

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.manager.createElmManager
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

abstract class ElmComponentFragment<Event : Any, Effect : Any, State : Any> : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elmManager =
        createElmManager(
            storeHolderFactory = ::createStoreHolder,
            storeCreator = ::createStore,
            initEventProvider = { initEvent },
        )

    protected val store
        get() = elmManager.store

    open fun createStoreHolder(
        storeProvider: () -> Store<Event, Effect, State>
    ): StoreHolder<Event, Effect, State> =
        LifecycleAwareStoreHolder(
            lifecycle = lifecycle,
            storeProvider = storeProvider,
        )

    abstract val initEvent: Event
    abstract fun createStore(): Store<Event, Effect, State>
}
