package vivid.money.elmslie.android.base

import androidx.appcompat.app.AppCompatActivity
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder
import vivid.money.elmslie.android.storeholder.StoreHolder

abstract class ElmActivity<Event : Any, Effect : Any, State : Any> :
    AppCompatActivity(), ElmDelegate<Event, Effect, State> {

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { this }

    protected val store
        get() = storeHolder.store

    override val storeHolder: StoreHolder<Event, Effect, State> = LifecycleAwareStoreHolder(lifecycle, ::createStore)
}