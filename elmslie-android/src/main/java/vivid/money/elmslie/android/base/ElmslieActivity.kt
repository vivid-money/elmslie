package vivid.money.elmslie.android.base

import androidx.appcompat.app.AppCompatActivity
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store as ElmStore

abstract class ElmslieActivity<Event : Any, Effect : Any, State : Any, Store : ElmStore<Event, Effect, State>> :
    AppCompatActivity(), ElmDelegate<Event, Effect, State, Store> {

    @Suppress("LeakingThis")
    private val elm = ElmScreen(this, lifecycle) { this }

    protected val store by fastLazy { elm.store }
}