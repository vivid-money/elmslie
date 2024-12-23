package money.vivid.elmslie.core.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.testutil.model.Command
import money.vivid.elmslie.core.testutil.model.Effect
import money.vivid.elmslie.core.testutil.model.Event
import money.vivid.elmslie.core.testutil.model.State
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ElmStoreTest {

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
    fun `Should stop the store properly`() = runTest {
        val store = store(State())

        store.start()
        store.accept(Event())
        store.stop()
        advanceUntilIdle()
    }

    @Test
    fun `Should stop getting state updates when the store is stopped`() = runTest {
        val actor = object : Actor<Command, Event>() {
            override fun execute(command: Command): Flow<Event> =
                flow { emit(Event()) }.onEach { delay(1000) }
        }

        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        state { copy(value = state.value + 1) }
                        commands { +Command() }
                    }
                },
                actor = actor,
            ).start()

        val emittedStates = mutableListOf<State>()
        val collectJob = launch { store.states.toList(emittedStates) }
        store.accept(Event())
        advanceTimeBy(3500)
        store.stop()

        assertEquals(
            mutableListOf(
                State(0), // Initial state
                State(1), // State after receiving trigger Event
                State(2), // State after executing the first command
                State(3), // State after executing the second command
                State(4) // State after executing the third command
            ),
            emittedStates
        )
        collectJob.cancel()
    }

    @Test
    fun `Should update state when event is received`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        state { copy(value = event.value) }
                    }
                },
            )
                .start()

        assertEquals(
            State(0),
            store.states.value,
        )
        store.accept(Event(value = 10))
        advanceUntilIdle()

        assertEquals(State(10), store.states.value)
    }

    @Test
    fun `Should not update state when it's equal to previous one`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        state { copy(value = event.value) }
                    }
                },
            )
                .start()

        val emittedStates = mutableListOf<State>()
        val collectJob = launch { store.states.toList(emittedStates) }

        store.accept(Event(value = 0))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                State(0) // Initial state
            ),
            emittedStates
        )
        collectJob.cancel()
    }

    @Test
    fun `Should collect all emitted effects`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .start()

        val effects = mutableListOf<Effect>()
        val collectJob = launch { store.effects.toList(effects) }
        store.accept(Event(value = 1))
        store.accept(Event(value = -1))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                Effect(value = 1), // The first effect
                Effect(value = -1), // The second effect
            ),
            effects
        )
        collectJob.cancel()
    }

    @Test
    fun `Should skip the effect which is emitted before subscribing to effects`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .start()

        val effects = mutableListOf<Effect>()
        store.accept(Event(value = 1))
        runCurrent()
        val collectJob = launch { store.effects.toList(effects) }
        store.accept(Event(value = -1))
        runCurrent()

        assertEquals(
            mutableListOf(
                Effect(value = -1),
            ),
            effects
        )
        collectJob.cancel()
    }

    @Test
    fun `Should collect all effects emitted once per time`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects {
                            +Effect(value = event.value)
                            +Effect(value = event.value)
                        }
                    }
                },
            )
                .start()

        val effects = mutableListOf<Effect>()
        val collectJob = launch { store.effects.toList(effects) }
        store.accept(Event(value = 1))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                Effect(value = 1), // The first effect
                Effect(value = 1), // The second effect
            ),
            effects
        )
        collectJob.cancel()
    }

    @Test
    fun `Should collect all emitted effects by all collectors`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .start()

        val effects1 = mutableListOf<Effect>()
        val effects2 = mutableListOf<Effect>()
        val collectJob1 = launch { store.effects.toList(effects1) }
        val collectJob2 = launch { store.effects.toList(effects2) }
        store.accept(Event(value = 1))
        store.accept(Event(value = -1))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                Effect(value = 1), // The first effect
                Effect(value = -1), // The second effect
            ),
            effects1
        )
        assertEquals(
            mutableListOf(
                Effect(value = 1), // The first effect
                Effect(value = -1), // The second effect
            ),
            effects2
        )
        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun `Should collect duplicated effects`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .start()

        val effects = mutableListOf<Effect>()
        val collectJob = launch { store.effects.toList(effects) }
        store.accept(Event(value = 1))
        store.accept(Event(value = 1))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                Effect(value = 1),
                Effect(value = 1),
            ),
            effects
        )
        collectJob.cancel()
    }

    @Test
    fun `Should collect event caused by actor`() = runTest {
        val actor = object : Actor<Command, Event>() {
            override fun execute(command: Command): Flow<Event> = flowOf(Event(command.value))
        }
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        state { copy(value = event.value) }
                        commands {
                            +Command(event.value - 1).takeIf { event.value > 0 }
                        }
                    }
                },
                actor = actor,
            )
                .start()

        val states = mutableListOf<State>()
        val collectJob = launch { store.states.toList(states) }

        store.accept(Event(3))
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                State(0), // Initial state
                State(3), // State after receiving Event with command number
                State(2), // State after executing the first command
                State(1), // State after executing the second command
                State(0) // State after executing the third command
            ),
            states
        )

        collectJob.cancel()
    }

    private fun store(
        state: State,
        reducer: StateReducer<Event, State, Effect, Command> = NoOpReducer(),
        actor: Actor<Command, Event> = NoOpActor()
    ) = ElmStore(state, reducer, actor)
}
