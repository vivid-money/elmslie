package money.vivid.elmslie.samples.calculator

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import money.vivid.elmslie.core.config.ElmslieConfig

@OptIn(ExperimentalCoroutinesApi::class)
internal class StoreTest {

    @BeforeTest
    fun beforeEach() {
        val testDispatcher = StandardTestDispatcher()
        ElmslieConfig.elmDispatcher { testDispatcher }
        Dispatchers.setMain(testDispatcher)
    }


    @AfterTest
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `1 + 1 = 2`() = runTest {
        val calculator = Calculator()
        val errors = mutableListOf<Effect>()
        val results = mutableListOf<Effect>()

        val errorsJob = launch { calculator.errors().toList(errors) }
        val resultJob = launch { calculator.results().toList(results) }

        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.evaluate()

        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NotifyNewResult(1), Effect.NotifyNewResult(2)), results)
        assertEquals(emptyList<Effect>(), errors)

        errorsJob.cancel()
        resultJob.cancel()
    }

    @Test
    fun `1 + 1 + 1 = 3`() = runTest {
        val calculator = Calculator()
        val errors = mutableListOf<Effect>()
        val results = mutableListOf<Effect>()

        val errorsJob = launch { calculator.errors().toList(errors) }
        val resultJob = launch { calculator.results().toList(results) }

        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.plus()
        calculator.digit('1')
        calculator.evaluate()

        advanceUntilIdle()

        assertEquals(
            listOf<Effect>(
                Effect.NotifyNewResult(1),
                Effect.NotifyNewResult(2),
                Effect.NotifyNewResult(3),
            ),
            results
        )
        assertEquals(emptyList<Effect>(), errors)

        errorsJob.cancel()
        resultJob.cancel()
    }

    @Test
    fun `1 + 2 times 3 minus 4 div 5 = 1`() = runTest {
        val calculator = Calculator()
        val errors = mutableListOf<Effect>()
        val results = mutableListOf<Effect>()

        val errorsJob = launch { calculator.errors().toList(errors) }
        val resultJob = launch { calculator.results().toList(results) }

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

        advanceUntilIdle()

        assertEquals(
            listOf<Effect>(
                Effect.NotifyNewResult(1),
                Effect.NotifyNewResult(3),
                Effect.NotifyNewResult(9),
                Effect.NotifyNewResult(5),
                Effect.NotifyNewResult(1)
            ),
            results
        )
        assertEquals(emptyList<Effect>(), errors)

        errorsJob.cancel()
        resultJob.cancel()
    }

    @Test
    fun `not a digit produces error`() = runTest {
        val calculator = Calculator()
        val errors = mutableListOf<Effect>()
        val results = mutableListOf<Effect>()

        val errorsJob = launch { calculator.errors().toList(errors) }
        val resultJob = launch { calculator.results().toList(results) }

        calculator.digit('x')

        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NotifyError("x is not a digit")), errors)
        assertEquals(emptyList<Effect>(), results)

        errorsJob.cancel()
        resultJob.cancel()
    }

    @Test
    fun `10 digits produces error`() = runTest {
        val calculator = Calculator()
        val errors = mutableListOf<Effect>()
        val results = mutableListOf<Effect>()

        val errorsJob = launch { calculator.errors().toList(errors) }
        val resultJob = launch { calculator.results().toList(results) }

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

        advanceUntilIdle()

        assertEquals(listOf<Effect>(Effect.NotifyError("Reached max input length")), errors)
        assertEquals(emptyList<Effect>(), results)

        errorsJob.cancel()
        resultJob.cancel()
    }
}
