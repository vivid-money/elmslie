package vivid.money.elmslie.core.store.exception

import java.lang.IllegalStateException

class StoreAlreadyStartedException : IllegalStateException(
    "Store is already started. Usually, it happens inside StoreHolder."
)
