package vivid.money.elmslie.android.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import vivid.money.elmslie.android.androidstore.store
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.core.store.Store

@Deprecated("Please use ElmRenderer and ViewBasedStoreStarter in you base classes. Will be removed in next releases. ")
abstract class ElmFragment<Event : Any, Effect : Any, State : Any> :
    Fragment, ElmRendererDelegate<Effect, State> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val renderer =
        ElmRenderer(
            delegate = this,
            screenLifecycle = lifecycle,
        )

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val starter =
        ViewBasedStoreStarter(
            storeProvider = { store },
            screenLifecycle = lifecycle,
            initEventProvider = { initEvent },
        )

    override val store by store(
        storeFactory = ::createStore,
    )

    abstract val initEvent: Event
    abstract fun createStore(savedStateHandle: SavedStateHandle): Store<Event, Effect, State>
}
