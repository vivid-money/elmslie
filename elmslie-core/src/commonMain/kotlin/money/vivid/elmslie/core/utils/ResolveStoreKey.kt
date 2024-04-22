package money.vivid.elmslie.core.utils

import money.vivid.elmslie.core.store.StateReducer

internal expect fun resolveStoreKey(reducer: StateReducer<*, *, *, *>): String