package vivid.money.elmslie.android.base

import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder

abstract class ElmFragment<Event : Any, Effect : Any, State : Any> : Fragment(), ElmDelegate<Event, Effect, State> {

    @Suppress("LeakingThis")
    private val elm = ElmScreen(this, lifecycle) { requireActivity() }

    protected val store
        get() = storeHolder.store

    override val storeHolder: StoreHolder<Event, Effect, State> = LifecycleAwareStoreHolder(lifecycle, ::createStore)
}