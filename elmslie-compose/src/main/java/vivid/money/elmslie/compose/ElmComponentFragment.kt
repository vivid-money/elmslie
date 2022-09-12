package vivid.money.elmslie.compose

import androidx.annotation.LayoutRes
import androidx.compose.runtime.*
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder

abstract class ElmComponentFragment<Event : Any, Effect : Any, State : Any> :
    Fragment, ElmDelegate<Event, Effect, State> {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { requireActivity() }

    protected val store
        get() = storeHolder.store

    override val storeHolder = LifecycleAwareStoreHolder(lifecycle) { createStore()!! }

    final override fun render(state: State) = Unit
}
