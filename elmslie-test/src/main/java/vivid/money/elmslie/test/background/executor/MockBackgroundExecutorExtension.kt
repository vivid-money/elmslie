package vivid.money.elmslie.test.background.executor

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import vivid.money.elmslie.core.config.ElmslieConfig
import java.util.concurrent.ScheduledExecutorService

/**
 * Mocks background executor for running all commands.
 *
 * Correct registration example:
 * ```
 * class Test {
 *
 *   @JvmField
 *   @RegisterExtension
 *   val executorExtension = MockBackgroundExecutorExtension()
 * }
 * ```
 */
class MockBackgroundExecutorExtension(
    private val backgroundExecutor: ScheduledExecutorService = SameThreadExecutorService()
) : BeforeEachCallback, AfterEachCallback {

    private lateinit var service: ScheduledExecutorService

    override fun beforeEach(context: ExtensionContext?) {
        service = ElmslieConfig.backgroundExecutor
        ElmslieConfig.backgroundExecutor { backgroundExecutor }
    }

    override fun afterEach(context: ExtensionContext?) {
        ElmslieConfig.backgroundExecutor { service }
    }
}
