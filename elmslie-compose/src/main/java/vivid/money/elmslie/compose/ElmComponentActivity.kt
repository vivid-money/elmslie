package vivid.money.elmslie.compose

import androidx.activity.ComponentActivity
import androidx.annotation.LayoutRes
import androidx.lifecycle.SavedStateHandle
import vivid.money.elmslie.android.androidstore.store
import vivid.money.elmslie.core.store.Store

abstract class ElmComponentActivity<Event : Any, Effect : Any, State : Any> : ComponentActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    protected val store by store(
        storeFactory = ::createStore,
    )

    abstract fun createStore(stateHandle: SavedStateHandle): Store<Event, Effect, State>
}
