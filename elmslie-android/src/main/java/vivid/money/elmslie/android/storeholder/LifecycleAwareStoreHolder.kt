package vivid.money.elmslie.android.storeholder

import androidx.lifecycle.*
import vivid.money.elmslie.android.util.fastLazy
import vivid.money.elmslie.core.store.Store

class LifecycleAwareStoreHolder<Event : Any, Effect : Any, State : Any>(
    lifecycle: Lifecycle,
    storeProvider: () -> Store<Event, Effect, State>,
) : StoreHolder<Event, Effect, State> {

    override val store by fastLazy { storeProvider().start() }

    private val lifecycleObserver =
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                store.stop()
            }
        }

    init {
        lifecycle.addObserver(lifecycleObserver)
    }
}
