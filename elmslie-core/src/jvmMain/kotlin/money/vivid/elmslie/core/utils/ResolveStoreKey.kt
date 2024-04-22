package money.vivid.elmslie.core.utils

import money.vivid.elmslie.core.store.StateReducer

internal actual fun resolveStoreKey(reducer: StateReducer<*, *, *, *>): String =
    (reducer::class.qualifiedName ?: reducer::class.simpleName)
        .orEmpty()
        .replace("Reducer", "Store")