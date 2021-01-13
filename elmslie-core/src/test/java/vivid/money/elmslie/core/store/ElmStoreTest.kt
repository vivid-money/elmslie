package vivid.money.elmslie.core.store

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.testutil.ext.NoOpActor
import vivid.money.elmslie.core.testutil.ext.NoOpReducer
import vivid.money.elmslie.core.testutil.ext.actor
import vivid.money.elmslie.core.testutil.ext.reducer
import vivid.money.elmslie.core.testutil.extension.TestSchedulerExtension
import vivid.money.elmslie.core.testutil.model.Command
import vivid.money.elmslie.core.testutil.model.Effect
import vivid.money.elmslie.core.testutil.model.Event
import vivid.money.elmslie.core.testutil.model.State
import java.util.concurrent.TimeUnit

class ElmStoreTest {

    private val scheduler = TestScheduler()

    @JvmField
    @RegisterExtension
    val extension = TestSchedulerExtension(scheduler)

    @Test
    fun `Disposing store works correctly`() {
        val store = store(State())

        store.start()
        store.accept(Event())
        store.dispose()

        assert(store.isDisposed)
    }

    @Test
    fun `Disposing store stops state`() {
        val store = store(
            State(),
            reducer { _, state ->
                Result(state = state.copy(value = state.value + 1), command = Command())
            },
            actor { Observable.timer(2, TimeUnit.SECONDS).map { Event() } }
        ).start()

        val observer = store.states.test()
        store.accept(Event())
        scheduler.advanceTimeBy(6, TimeUnit.SECONDS)
        store.dispose()

        observer.assertValues(
            State(0), // Initial
            State(1), // After receiving initial Event
            State(2), // After executing first command
            State(3), // After executing second command
            State(4)  // After executing third command
        )
    }

    @Test
    fun `Event triggers state update`() {
        val store = store(
            State(),
            reducer { event, state -> Result(state = state.copy(value = event.value)) }
        ).start()

        val observer = store.states.test()
        store.accept(Event(value = 10))
        scheduler.triggerActions()

        observer.assertValues(
            State(0),
            State(10)
        )
    }

    @Test
    fun `Not changed state is not emitted`() {
        val store = store(
            State(),
            reducer { event, state -> Result(state = state.copy(value = event.value)) }
        ).start()

        val observer = store.states.test()

        store.accept(Event(value = 0))
        scheduler.triggerActions()

        observer.assertValues(State(0))
    }

    @Test
    fun `Emitted effect is received by observers`() {
        val store = store(
            State(),
            reducer { event, state -> Result(state = state, effect = Effect(value = event.value)) }
        ).start()

        val observer = store.effects.test()
        store.accept(Event(value = 7))
        store.accept(Event(value = 9))
        scheduler.triggerActions()

        observer.assertValues(Effect(value = 7), Effect(value = 9))
    }

    @Test
    fun `Command result is observed by store`() {
        val store = store(
            State(),
            reducer { event, state ->
                Result(
                    state = state.copy(value = event.value),
                    command = Command(event.value - 1).takeIf { event.value > 0 }
                )
            },
            actor { Observable.just(Event(it.value)) }
        ).start()

        val observer = store.states.test()

        store.accept(Event(3))
        scheduler.triggerActions()
        store.dispose()

        observer.assertValues(
            State(0), // Initial
            State(3), // After receiving initial Event
            State(2), // After executing first command
            State(1), // After executing second command
            State(0)  // After executing third command
        )
    }

    private fun store(
        state: State,
        reducer: StateReducer<Event, State, Effect, Command> = NoOpReducer(),
        actor: Actor<Command, Event> = NoOpActor()
    ) = ElmStore(state, reducer, actor)
}