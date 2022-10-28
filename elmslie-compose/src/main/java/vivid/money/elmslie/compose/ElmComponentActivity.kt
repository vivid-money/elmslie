package vivid.money.elmslie.compose

import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder

abstract class ElmComponentActivity<Event : Any, Effect : Any, State : Any> :
    ComponentActivity, ElmDelegate<Event, Effect, State> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { this }

    val store
        get() = storeHolder.store

    override val storeHolder = LifecycleAwareStoreHolder(lifecycle) { createStore()!! }

    final override fun render(state: State) = Unit
}
