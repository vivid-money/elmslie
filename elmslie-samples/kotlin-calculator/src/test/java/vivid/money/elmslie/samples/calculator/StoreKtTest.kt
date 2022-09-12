package vivid.money.elmslie.samples.calculator

import io.reactivex.rxjava3.schedulers.TestScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.test.TestSchedulerExtension
import vivid.money.elmslie.test.background.executor.MockBackgroundExecutorExtension
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoreKtTest {

    private val scheduler = TestScheduler()

    @JvmField @RegisterExtension val schedulerExtension = TestSchedulerExtension(scheduler)

    @JvmField @RegisterExtension val executorExtension = MockBackgroundExecutorExtension()

    @JvmField @RegisterExtension val testDispatcherExtension = TestDispatcherExtension()

    @Test
    fun `1 + 1 = 2`() = runTest {
        val calculator = Calculator()
        val errors = calculator.errors().test()
        val results = calculator.results().test()

        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.evaluate()

        scheduler.triggerActions()
        advanceUntilIdle()

        results.assertValues(Effect.NotifyNewResult(1), Effect.NotifyNewResult(2))
        errors.assertEmpty()
    }

    @Test
    fun `1 + 1 + 1 = 3`() = runTest {
        val calculator = Calculator()
        val errors = calculator.errors().test()
        val results = calculator.results().test()

        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.evaluate()

        scheduler.triggerActions()
        advanceUntilIdle()

        results.assertValues(
            Effect.NotifyNewResult(1),
            Effect.NotifyNewResult(2),
            Effect.NotifyNewResult(3)
        )
        errors.assertEmpty()
    }

    @Test
    fun `1 + 2 times 3 minus 4 div 5 = 1`() = runTest {
        val calculator = Calculator()
        val errors = calculator.errors().test()
        val results = calculator.results().test()

        calculator.digit('1')
        calculator.plus()
        calculator.digit('2')
        calculator.times()
        calculator.digit('3')
        calculator.minus()
        calculator.digit('4')
        calculator.divide()
        calculator.digit('5')
        calculator.evaluate()

        scheduler.triggerActions()
        advanceUntilIdle()

        results.assertValues(
            Effect.NotifyNewResult(1),
            Effect.NotifyNewResult(3),
            Effect.NotifyNewResult(9),
            Effect.NotifyNewResult(5),
            Effect.NotifyNewResult(1)
        )
        errors.assertEmpty()
    }

    @Test
    fun `not a digit produces error`() = runTest  {
        val calculator = Calculator()
        val errors = calculator.errors().test()
        val results = calculator.results().test()

        calculator.digit('x')

        scheduler.triggerActions()
        advanceUntilIdle()

        results.assertEmpty()
        errors.assertValue(Effect.NotifyError("x is not a digit"))
    }

    @Test
    fun `10 digits produces error`() = runTest {
        val calculator = Calculator()
        val errors = calculator.errors().test()
        val results = calculator.results().test()

        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')
        calculator.digit('1')

        scheduler.triggerActions()
        advanceUntilIdle()

        results.assertEmpty()
        errors.assertValue(Effect.NotifyError("Reached max input length"))
    }
}
