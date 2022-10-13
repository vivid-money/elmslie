package vivid.money.elmslie.compose

import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.lifecycle.SavedStateHandle
import vivid.money.elmslie.android.manager.createElmManager
import vivid.money.elmslie.core.store.Store

abstract class ElmComponentActivity<Event : Any, Effect : Any, State : Any> : ComponentActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elmManager =
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
