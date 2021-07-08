package vivid.money.elmslie.compose

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava3.subscribeAsState
import vivid.money.elmslie.android.screen.ElmDelegate
import vivid.money.elmslie.android.screen.ElmScreen
import vivid.money.elmslie.android.storeholder.LifecycleAwareStoreHolder

abstract class ElmComponentActivity<Event : Any, Effect : Any, State : Any> :
    ComponentActivity(), ElmDelegate<Event, Effect, State> {

    @Suppress("LeakingThis", "UnusedPrivateMember")
    private val elm = ElmScreen(this, lifecycle) { this }

    val store
        get() = storeHolder.store

    override val storeHolder = LifecycleAwareStoreHolder(lifecycle, ::createStore)

    @Composable
    fun state() = store.states.subscribeAsState(initial = store.currentState)

    @Composable
    fun effect() = store.effects.map { EffectWithKey(it) }.subscribeAsState(initial = null)

    final override fun render(state: State) = Unit
}

