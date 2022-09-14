package vivid.money.elmslie.core

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vivid.money.elmslie.core.config.ElmslieConfig

fun ElmScope(name: String): CoroutineScope =
    CoroutineScope(ElmslieConfig.ioDispatchers + SupervisorJob() + CoroutineName(name))
