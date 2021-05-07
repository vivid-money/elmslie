package vivid.money.elmslie.android.storeholder

import vivid.money.elmslie.core.store.Store

/**
 * Implementation of this interface should:
 *  1. call Store::start during store creation
 *  2. guarantee invariance of store while the view exists
 *  3. call Store::stop when store be ready to gc
 **/
interface StoreHolder<Event : Any, Effect : Any, State : Any> {

    val store: Store<Event, Effect, State>
}