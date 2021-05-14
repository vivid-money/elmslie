package vivid.money.elmslie.storepersisting

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import vivid.money.elmslie.android.storeholder.StoreHolder
import vivid.money.elmslie.core.store.Store

fun <Event : Any, Effect : Any, State : Any> ComponentActivity.retainStoreHolder(
    storeName: String = "${this::class.java.name}Store",
    storeProvider: () -> Store<Event, Effect, State>,
): Lazy<StoreHolder<Event, Effect, State>> = this.retain(storeName) { ClearableStoreHolder(storeProvider) }

fun <Event : Any, Effect : Any, State : Any> Fragment.retainStoreHolder(
    storeName: String = "${this::class.java.name}Store",
    storeProvider: () -> Store<Event, Effect, State>,
): Lazy<StoreHolder<Event, Effect, State>> = this.retain(storeName) { ClearableStoreHolder(storeProvider) }

fun <Event : Any, Effect : Any, State : Any> Fragment.retainInParentStoreHolder(
    storeName: String = "${this::class.java.name}Store",
    storeProvider: () -> Store<Event, Effect, State>,
): Lazy<StoreHolder<Event, Effect, State>> = this.retainInParent(storeName) { ClearableStoreHolder(storeProvider) }

fun <Event : Any, Effect : Any, State : Any> Fragment.retainInActivityStoreHolder(
    storeName: String = "${this::class.java.name}Store",
    storeProvider: () -> Store<Event, Effect, State>,
): Lazy<StoreHolder<Event, Effect, State>> = this.retainInActivity(storeName) { ClearableStoreHolder(storeProvider) }