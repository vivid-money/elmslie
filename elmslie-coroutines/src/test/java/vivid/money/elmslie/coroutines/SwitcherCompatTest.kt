package vivid.money.elmslie.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SwitcherCompatTest {

    @JvmField @RegisterExtension val testDispatcherExtension = TestDispatcherExtension()

    sealed interface Event {
        object First : Event
        object Second : Event
    }

    @Test
    fun `Switcher cancels previous request`() = runTest {
        val switcher = Switcher()

        val values = mutableListOf<Event>()

        val firstCollector = launch {
            switcher
                .switch<Event> {
                    flow {
                        delay(2000L)
                        emit(Event.First)
                    }
                }
                .collect { values.add(it) }
        }
        advanceTimeBy(500L)

        val secondCollector = launch {
            switcher.switch<Event> { flowOf(Event.Second) }.collect { values.add(it) }
        }

        advanceTimeBy(2500L)

        Assertions.assertEquals(
            listOf(Event.Second),
            values,
        )
        Assertions.assertEquals(
            true,
            firstCollector.isCompleted,
        )

        firstCollector.cancel()
        secondCollector.cancel()
    }

    @Test
    fun `Switcher cancels request if error`() = runTest {
        val switcher = Switcher()
        val values = mutableListOf<Event>()
        val firstCollector = launch {
            switcher.switch<Event> { flow { error("Error") } }.collect { values.add(it) }
        }
        advanceUntilIdle()

        Assertions.assertEquals(
            emptyList<Event>(),
            values,
        )
        Assertions.assertEquals(
            true,
            firstCollector.isCompleted,
        )

        firstCollector.cancel()
    }

    @Test
    fun `Switcher continue receiving updates`() = runTest {
        val switcher = Switcher()
        val values = mutableListOf<Event>()
        val firstCollector = launch {
            switcher
                .switch<Event> {
                    flow {
                        while (true) {
                            delay(100L)
                            emit(Event.First)
                        }
                    }
                }
                .collect { values.add(it) }
        }
        advanceTimeBy(510L)

        Assertions.assertEquals(
            listOf(
                Event.First,
                Event.First,
                Event.First,
                Event.First,
                Event.First,
            ),
            values,
        )
        Assertions.assertEquals(
            false,
            firstCollector.isCompleted,
        )

        firstCollector.cancel()
    }
}
