package vivid.money.elmslie.android.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle
import vivid.money.elmslie.android.fabric.elmStore
import vivid.money.elmslie.android.renderer.ElmRenderer
import vivid.money.elmslie.android.renderer.ElmRendererDelegate
import vivid.money.elmslie.android.storestarter.ViewBasedStoreStarter
import vivid.money.elmslie.core.store.Store

@Deprecated("Please use ElmRenderer and ViewBasedStoreStarter in you base classes. Will be removed in next releases. ")
abstract class ElmActivity<Event : Any, Effect : Any, State : Any> :
    AppCompatActivity, ElmRendererDelegate<Effect, State> {

    override val store by elmStore(
        storeFactory = ::createStore,
    )

    @Suppress("LeakingThis")
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

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    abstract val initEvent: Event
    abstract fun createStore(savedStateHandle: SavedStateHandle): Store<Event, Effect, State>
}
