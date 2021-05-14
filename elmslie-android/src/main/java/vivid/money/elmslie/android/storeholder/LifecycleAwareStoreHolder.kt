package vivid.money.elmslie.android.storeholder

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store

class LifecycleAwareStoreHolder<Event : Any, Effect : Any, State : Any>(
    lifecycle: Lifecycle,
    storeProvider: () -> Store<Event, Effect, State>,
) : StoreHolder<Event, Effect, State> {

    override val store by fastLazy { storeProvider().start() }

    private val lifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            store.stop()
        }
    }

    init {
        lifecycle.addObserver(lifecycleObserver)
    }
}