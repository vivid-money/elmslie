package vivid.money.elmslie.android.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import vivid.money.elmslie.android.screen.MviDelegate
import vivid.money.elmslie.android.screen.ScreenMvi
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.utils.disposable.DisposableDelegate
import vivid.money.elmslie.utils.disposable.DisposableDelegateImpl
import vivid.money.elmslie.core.store.Store as ElmStore

abstract class ElmslieActivity<Event : Any, Effect : Any, State : Any, Store : ElmStore<Event, Effect, State>> :
    AppCompatActivity(),
    DisposableDelegate by DisposableDelegateImpl(),
    MviDelegate<Event, Effect, State, Store> {

    @Suppress("LeakingThis")
    private val mvi = ScreenMvi(this) { this }

    override val screenLifecycle: Lifecycle
        get() = lifecycle

    protected val store by fastLazy { mvi.store }

    override fun onDestroy() {
        super.onDestroy()
        clearDisposables()
    }
}