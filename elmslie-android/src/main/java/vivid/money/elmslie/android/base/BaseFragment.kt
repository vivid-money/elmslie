package vivid.money.elmslie.android.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import vivid.money.elmslie.android.screen.MviDelegate
import vivid.money.elmslie.android.screen.ScreenMvi
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store as ElmStore

abstract class ElmslieFragment<Event : Any, Effect : Any, State : Any, Store : ElmStore<Event, Effect, State>> :
    Fragment(), MviDelegate<Event, Effect, State, Store> {

    @Suppress("LeakingThis")
    private val mvi = ScreenMvi(this) { requireActivity() }

    override val screenLifecycle: Lifecycle
        get() = lifecycle

    protected val store by fastLazy { mvi.store }
}