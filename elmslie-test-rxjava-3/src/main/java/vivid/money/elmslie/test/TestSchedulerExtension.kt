package vivid.money.elmslie.test

import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Replaces all RxJava schedulers with a [TestScheduler].
 * NOTE: You need to initialize object lazily to use this rule!
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
