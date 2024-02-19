package money.vivid.elmslie.core.store

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
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import money.vivid.elmslie.core.config.ElmslieConfig
import money.vivid.elmslie.core.testutil.model.Command
import money.vivid.elmslie.core.testutil.model.Effect
import money.vivid.elmslie.core.testutil.model.Event
import money.vivid.elmslie.core.testutil.model.State

@OptIn(ExperimentalCoroutinesApi::class)
class EffectCachingElmStoreTest {

    @BeforeTest
    fun beforeEach() {
        val testDispatcher = StandardTestDispatcher()
        ElmslieConfig.ioDispatchers { testDispatcher }
        Dispatchers.setMain(testDispatcher)
    }


    @AfterTest
    fun afterEach() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should collect effects which are emitted before collecting flow`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .toCachedStore()

        store.start()
        store.accept(Event(value = 1))
        store.accept(Event(value = 2))
        store.accept(Event(value = 2))
        advanceUntilIdle()

        val effects = mutableListOf<Effect>()
        val job = launch { store.effects.toList(effects) }
        advanceUntilIdle()

        assertEquals(
            listOf(
                Effect(value = 1),
                Effect(value = 2),
                Effect(value = 2),
            ),
            effects
        )

        job.cancel()
    }

    @Test
    fun `Should collect effects which are emitted before collecting flow and after`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .toCachedStore()

        store.start()
        store.accept(Event(value = 1))
        store.accept(Event(value = 2))
        store.accept(Event(value = 2))
        advanceUntilIdle()

        val effects = mutableListOf<Effect>()
        val job = launch { store.effects.toList(effects) }
        store.accept(Event(value = 3))
        advanceUntilIdle()

        assertEquals(
            listOf(
                Effect(value = 1),
                Effect(value = 2),
                Effect(value = 2),
                Effect(value = 3),
            ),
            effects
        )

        job.cancel()
    }

    @Test
    fun `Should emit effects from cache only for the first subscriber`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .toCachedStore()

        store.start()
        store.accept(Event(value = 1))
        advanceUntilIdle()

        val effects1 = mutableListOf<Effect>()
        val effects2 = mutableListOf<Effect>()
        val job1 = launch { store.effects.toList(effects1) }
        runCurrent()
        val job2 = launch { store.effects.toList(effects2) }
        runCurrent()

        assertEquals(
            listOf(
                Effect(value = 1),
            ),
            effects1
        )

        assertEquals(emptyList<Effect>(), effects2)

        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `Should cache effects if there is no left collectors`() = runTest {
        val store =
            store(
                state = State(),
                reducer = object : StateReducer<Event, State, Effect, Command>() {
                    override fun Result.reduce(event: Event) {
                        effects { +Effect(value = event.value) }
                    }
                },
            )
                .toCachedStore()

        store.start()
        val effects = mutableListOf<Effect>()
        var job1 = launch { store.effects.toList(effects) }
        runCurrent()
        job1.cancel()
        store.accept(Event(value = 2))
        runCurrent()
        job1 = launch { store.effects.toList(effects) }
        runCurrent()

        assertEquals(
            listOf(
                Effect(value = 2),
            ),
            effects
        )

        job1.cancel()
    }

    private fun store(
        state: State,
        reducer: StateReducer<Event, State, Effect, Command> = NoOpReducer(),
        actor: Actor<Command, Event> = NoOpActor()
    ) = ElmStore(state, reducer, actor)
}
