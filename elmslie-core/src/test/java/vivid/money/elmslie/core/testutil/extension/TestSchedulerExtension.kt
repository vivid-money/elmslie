package vivid.money.elmslie.core.testutil.extension

import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Replaces all RxJava schedulers with a [TestScheduler].
 * NOTE: You need to intitialize object lazily to use this rule!
 * Wrong usage example:
 * `
 * class Test {
 *   val classWithRxCode = ClassWithRxCode()
 * }
 * `
 * if `ClassWithRxCode` calls `Schedulers.io()` in it's constructor it won't be mocked because test extensions are
 * applied after Test class instance is created.
 */
class TestSchedulerExtension(
    val scheduler: TestScheduler = TestScheduler()
) : BeforeEachCallback, AfterEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
    }

    override fun afterEach(context: ExtensionContext?) {
        RxJavaPlugins.reset()
    }
}
