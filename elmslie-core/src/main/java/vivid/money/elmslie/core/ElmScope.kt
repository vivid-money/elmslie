package vivid.money.elmslie.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vivid.money.elmslie.core.config.ElmslieConfig
import kotlin.coroutines.CoroutineContext

@SuppressWarnings("detekt.FunctionNaming")
fun ElmBackgroundScope(name: String): CoroutineScope =
    CoroutineScope(
        context = ElmslieConfig.ioDispatchers + ElmContext(name),
    )

@SuppressWarnings("detekt.FunctionNaming")
fun ElmEventScope(name: String): CoroutineScope =
    CoroutineScope(
        context = ElmslieConfig.eventDispatchers + ElmContext(name),
    )

private fun ElmContext(name: String): CoroutineContext =
    SupervisorJob() +
            CoroutineName(name) +
            CoroutineExceptionHandler { _, throwable ->
                ElmslieConfig.logger.fatal("Unhandled error: $throwable")
            }
