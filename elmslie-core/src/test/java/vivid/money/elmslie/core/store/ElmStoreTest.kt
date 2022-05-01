package vivid.money.elmslie.core.store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.core.testutil.model.Command
import vivid.money.elmslie.core.testutil.model.Effect
import vivid.money.elmslie.core.testutil.model.Event
import vivid.money.elmslie.core.testutil.model.State
import vivid.money.elmslie.test.background.executor.MockBackgroundExecutorExtension
import java.util.concurrent.Executors

class ElmStoreTest {

    @JvmField
    @RegisterExtension
    val executorExtension = MockBackgroundExecutorExtension()

    @Test
    fun `Stopping the store works correctly`() {
        val store = store(State())

        store.start()
        store.accept(Event())
        store.stop()

        assert(!store.isStarted)
    }

    @Test
    fun `Stopping the store stops state`() {
        val worker = Executors.newSingleThreadExecutor()
        val store = store(
            State(),
            { _, state ->
                Result(state = state.copy(value = state.value + 1), command = Command())
            },
            { _, onEvent, _ ->
                val future = worker.submit {
                    Thread.sleep(1000)
                    onEvent(Event())
                }
                Disposable { future.cancel(true) }
            }
        ).start()

        val states = mutableListOf<State>()
        store.states(states::add)
        store.accept(Event())
        Thread.sleep(3500)
        store.stop()

        assertEquals(
            mutableListOf(
                State(0), // Initial state
                State(1), // State after receiving trigger Event
                State(2), // State after executing the first command
                State(3), // State after executing the second command
                State(4)  // State after executing the third command
            ),
            states
        )
    }

    @Test
    fun `Event triggers state update`() {
        val store = store(
            State(),
            { event, state -> Result(state = state.copy(value = event.value)) }
        ).start()

        val states = mutableListOf<State>()
        store.states(states::add)
        store.accept(Event(value = 10))

        assertEquals(
            mutableListOf(
                State(0), // Initial state
                State(10) // State after receiving initial Event
            ),
            states
        )
    }

    @Test
    fun `Not changed state is not emitted`() {
        val store = store(
            State(),
            { event, state -> Result(state = state.copy(value = event.value)) }
        ).start()

        val states = mutableListOf<State>()
        store.states(states::add)

        store.accept(Event(value = 0))

        assertEquals(
            mutableListOf(
                State(0) // Initial state
            ),
            states
        )
    }

    @Test
    fun `Emitted effect is received by observers`() {
        val store = store(
            State(),
            { event, state ->
                Result(state = state, effect = Effect(value = event.value))
            }
        ).start()

        val effects = mutableListOf<Effect>()
        store.effects(effects::add)
        store.accept(Event(value = 1))
        store.accept(Event(value = -1))

        assertEquals(
            mutableListOf(
                Effect(value = 1), // The first effect
                Effect(value = -1), // The second effect
            ),
            effects
        )
    }

    @Test
    fun `Command result is observed by store`() {
        val store = store(
            State(),
            { event, state ->
                Result(
                    state = state.copy(value = event.value),
                    command = Command(event.value - 1).takeIf { event.value > 0 }
                )
            },
            { command, onEvent, _ ->
                onEvent(Event(command.value))
                Disposable {}
            }
        ).start()

        val states = mutableListOf<State>()
        store.states(states::add)

        store.accept(Event(3))
        store.stop()

        assertEquals(
            mutableListOf(
                State(0), // Initial state
                State(3), // State after receiving Event with command number
                State(2), // State after executing the first command
                State(1), // State after executing the second command
                State(0)  // State after executing the third command
            ),
            states
        )
    }

    private fun store(
        state: State,
        reducer: StateReducer<Event, State, Effect, Command> = NoOpReducer(),
        actor: DefaultActor<Command, Event> = NoOpActor()
    ) = ElmStore(state, reducer, actor)
}
