package vivid.money.elmslie.compose

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import vivid.money.elmslie.android.manager.ElmManager
import vivid.money.elmslie.android.manager.createElmManager
import vivid.money.elmslie.core.store.Store

abstract class ElmComponentFragment<Event : Any, Effect : Any, State : Any> : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elmManager: ElmManager<Event, Effect, State> =
        createElmManager(
            storeFactory = ::createStore,
            initEventProvider = { initEvent },
            key = this::class.java.name,
        )

    protected val store
        get() = elmManager.store

    abstract val initEvent: Event
    abstract fun createStore(stateHandle: SavedStateHandle): Store<Event, Effect, State>
}
