package vivid.money.elmslie.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vivid.money.elmslie.core.config.ElmslieConfig

@SuppressWarnings("detekt.FunctionNaming")
fun ElmScope(name: String): CoroutineScope =
    CoroutineScope(
        context =
            ElmslieConfig.ioDispatchers +
                SupervisorJob() +
                CoroutineName(name) +
                CoroutineExceptionHandler { _, throwable ->
                    ElmslieConfig.logger.debug("Unhandled error: $throwable")
                },
    )
