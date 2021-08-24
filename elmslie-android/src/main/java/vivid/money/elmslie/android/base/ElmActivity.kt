package vivid.money.elmslie.android.base

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.android.util.fastLazy

abstract class ElmActivity<Event : Any, Effect : Any, State : Any> :
    AppCompatActivity, ElmDelegate<Event, Effect, State> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { this }

    override var isAllowedToRunMvi: Boolean = true

    protected val store
        get() = storeHolder.store

    override val storeHolder: StoreHolder<Event, Effect, State> by fastLazy {
        LifecycleAwareStoreHolder(lifecycle, ::createStore)
    }
}
