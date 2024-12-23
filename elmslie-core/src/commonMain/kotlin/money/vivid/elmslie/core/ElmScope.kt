package money.vivid.elmslie.core

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import money.vivid.elmslie.core.config.ElmslieConfig

@Suppress("detekt.FunctionNaming")
fun ElmScope(name: String): CoroutineScope =
    CoroutineScope(
        context =
            ElmslieConfig.elmDispatcher +
                SupervisorJob() +
                CoroutineName(name) +
                CoroutineExceptionHandler { _, throwable ->
                    ElmslieConfig.logger.fatal("Unhandled error: $throwable")
                },
    )
