package money.vivid.elmslie.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val IoDispatcher: CoroutineDispatcher = Dispatchers.IO