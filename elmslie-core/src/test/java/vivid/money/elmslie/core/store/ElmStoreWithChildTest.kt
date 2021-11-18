package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.store.binding.coordinates
import vivid.money.elmslie.core.testutil.model.ChildCommand
import vivid.money.elmslie.core.testutil.model.ChildEffect
import vivid.money.elmslie.core.testutil.model.ChildEvent
import vivid.money.elmslie.core.testutil.model.ChildState
import vivid.money.elmslie.core.testutil.model.ParentCommand
import vivid.money.elmslie.core.testutil.model.ParentEffect
import vivid.money.elmslie.core.testutil.model.ParentEvent
import vivid.money.elmslie.core.testutil.model.ParentState
import vivid.money.elmslie.test.TestSchedulerExtension

class ElmStoreWithChildTest {

    private val scheduler = TestScheduler()

    @JvmField
    @RegisterExtension
    val extension = TestSchedulerExtension(scheduler)

    @Test
    fun `Parent event is propagated to child and state update is received afterwards`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                when (event) {
                    is ParentEvent.Plain ->
                        Result(state = state.copy(value = 10), effect = ParentEffect)
                    is ParentEvent.ChildUpdated ->
                        Result(state = state.copy(childValue = event.state.value))
                }
            }
        )
        val child = childStore(
            ChildState(),
            { _, state -> Result(state.copy(value = 100), effect = ChildEffect) }
        )
        parent.coordinates(
            child,
            dispatching = {
                states { ChildEvent.First }
            },
            receiving = {
                states { ParentEvent.ChildUpdated(this) }
                effects { ParentEvent.Plain }
            }
        ).start()

        val observer = parent.states.test()

        parent.accept(ParentEvent.Plain)

        scheduler.triggerActions()

        observer.assertValues(
            ParentState(0, 0),
            ParentState(10, 0),
            ParentState(10, 100)
        )
    }

    @Test
    fun `Parent effect is propagated to child and effect is received afterwards`() {
        val parent = parentStore(
            ParentState(),
            { _, state -> Result(state, effect = ParentEffect) }
        )
        val child = childStore(
            ChildState(),
            { _, state -> Result(state.copy(value = 100), effect = ChildEffect) }
        )
        parent.coordinates(
            child,
            dispatching = {
                effects { ChildEvent.First }
            }
        ).start()

        val observer = child.effects.test()

        parent.accept(ParentEvent.Plain)

        scheduler.triggerActions()

        observer.assertValues(ChildEffect)
    }

    @Test
    fun `Child state update after action is propagated to the parent`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                if (event is ParentEvent.ChildUpdated) {
                    Result(state = state.copy(childValue = event.state.value))
                } else {
                    Result(state = state.copy(value = 10), effect = ParentEffect)
                }
            }
        )
        val child = childStore(
            ChildState(),
            { event, state ->
                if (event == ChildEvent.First) {
                    Result(state.copy(value = 100), command = ChildCommand)
                } else {
                    Result(state.copy(value = 200))
                }
            },
            { Observable.just(ChildEvent.Second) }
        )
        parent.coordinates(
            child,
            dispatching = {
                effects { ChildEvent.First }
            },
            receiving = {
                states { ParentEvent.ChildUpdated(this) }
            }
        ).start()

        val observer = parent.states.test()

        parent.accept(ParentEvent.Plain)

        scheduler.triggerActions()

        observer.assertValues(
            ParentState(0, 0),
            ParentState(10, 0),
            ParentState(10, 100),
            ParentState(10, 200)
        )
    }

    @Test
    fun `Stopping the binding stops both parent and child`() {
        val parent = parentStore(ParentState())
        val child = childStore(ChildState())
        val binding = parent.coordinates(child).start()

        binding.stop()

        assert(!parent.isStarted)
        assert(!child.isStarted)
    }

    @Test
    fun `Child command results are received by parents consecutively`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                if (event is ParentEvent.ChildUpdated) {
                    Result(state = state.copy(childValue = event.state.value))
                } else {
                    Result(state = state, effect = ParentEffect)
                }
            }
        )
        val child = childStore(
            ChildState(),
            { event, state ->
                when (event) {
                    ChildEvent.First -> Result(state.copy(value = 100), command = ChildCommand)
                    ChildEvent.Second -> Result(state.copy(value = 200))
                    ChildEvent.Third -> Result(state.copy(value = 300))
                }
            },
            { Observable.just(ChildEvent.Second, ChildEvent.Third) }
        )
        parent.coordinates(
            child,
            dispatching = {
                effects { ChildEvent.First }
            },
            receiving = {
                states { ParentEvent.ChildUpdated(this) }
            }
        ).start()

        val observer = parent.states.test()

        parent.accept(ParentEvent.Plain)

        scheduler.triggerActions()

        observer.assertValues(
            ParentState(0, 0),
            ParentState(0, 100),
            ParentState(0, 200),
            ParentState(0, 300)
        )
    }

    private fun parentStore(
        state: ParentState,
        reducer: StateReducer<ParentEvent, ParentState, ParentEffect, ParentCommand> = NoOpReducer(),
        actor: Actor<ParentCommand, ParentEvent> = NoOpActor()
    ): Store<ParentEvent, ParentEffect, ParentState> = ElmStore(state, reducer, actor)

    private fun childStore(
        state: ChildState,
        reducer: StateReducer<ChildEvent, ChildState, ChildEffect, ChildCommand> = NoOpReducer(),
        actor: Actor<ChildCommand, ChildEvent> = NoOpActor()
    ): Store<ChildEvent, ChildEffect, ChildState> = ElmStore(state, reducer, actor)
}
