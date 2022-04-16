package vivid.money.elmslie.core.store

import org.junit.jupiter.api.Assertions.assertEquals
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
import vivid.money.elmslie.core.disposable.Disposable
import vivid.money.elmslie.test.background.executor.MockBackgroundExecutorExtension

class ElmStoreWithChildTest {

    @JvmField
    @RegisterExtension
    val executorExtension = MockBackgroundExecutorExtension()

    @Test
    fun `Parent event is propagated to child and state update is received afterwards`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                when (event) {
                    is ParentEvent.Plain ->
                        Result(state = state.copy(value = 10), effect = ParentEffect.ToChild(ChildEvent.First))
                    is ParentEvent.ChildUpdated ->
                        Result(state = state.copy(childValue = event.state.value))
                }
            }
        )
        val child = childStore(
            ChildState(),
            { _, state ->
                Result(state.copy(value = 100), effect = ChildEffect)
            }
        )
        val coordination = parent.coordinates(
            child,
            dispatching = {
                states { ChildEvent.First }
            },
            receiving = {
                states { ParentEvent.ChildUpdated(this) }
                effects { ParentEvent.Plain }
            }
        )

        val values = mutableListOf<ParentState>()
        parent.states { values.add(it) }

        coordination.start()
        parent.accept(ParentEvent.Plain)

        assertEquals(
            mutableListOf(
                ParentState(0, 0),
                ParentState(0, 100),
                ParentState(10, 100)
            ),
            values
        )
    }

    @Test
    fun `Parent effect is propagated to child and effect is received afterwards`() {
        val parent = parentStore(
            ParentState(),
            { _, state -> Result(state, effect = ParentEffect.ToParent) }
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

        val values = mutableListOf<ChildEffect>()
        child.effects(values::add)

        parent.accept(ParentEvent.Plain)

        assertEquals(
            mutableListOf(ChildEffect),
            values,
        )
    }

    @Test
    fun `Child state update after action is propagated to the parent`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                if (event is ParentEvent.ChildUpdated) {
                    Result(state = state.copy(childValue = event.state.value))
                } else {
                    Result(state = state.copy(value = 10), effect = ParentEffect.ToParent)
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
            { command, onEvent, onError ->
                onEvent(ChildEvent.Second)
                Disposable {}
            }
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

        val values = mutableListOf<ParentState>()
        parent.states { values.add(it) }

        parent.accept(ParentEvent.Plain)

        assertEquals(
            mutableListOf(
                ParentState(0, 0),
                ParentState(10, 0),
                ParentState(10, 100),
                ParentState(10, 200)
            ),
            values,
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
                    Result(state = state, effect = ParentEffect.ToParent)
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
            { _, onEvent, _ ->
                onEvent(ChildEvent.Second)
                onEvent(ChildEvent.Third)
                Disposable { }
            }
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

        val values = mutableListOf<ParentState>()
        parent.states(values::add)

        parent.accept(ParentEvent.Plain)

        assertEquals(
            mutableListOf(
                ParentState(0, 0),
                ParentState(0, 100),
                ParentState(0, 200),
                ParentState(0, 300)
            ),
            values,
        )
    }

    @Test
    fun `Parent Effect is delivered when it's effect observation started`() {
        val parent = parentStore(
            ParentState(),
            { event, state ->
                if (event is ParentEvent.ChildUpdated) {
                    Result(state = state.copy(childValue = event.state.value))
                } else {
                    Result(
                        state = state.copy(value = 10),
                        commands = emptyList(),
                        effects = listOf(
                            ParentEffect.ToParent,
                            ParentEffect.ToChild(ChildEvent.First)
                        )
                    )
                }
            }
        )
        val child = childStore(
            ChildState(),
            { event, state ->
                when (event) {
                    ChildEvent.First -> Result(state.copy(value = 100))
                    ChildEvent.Second -> Result(state)
                    ChildEvent.Third -> Result(state)
                }
            }
        )
        val combined = parent.coordinates(
            child,
            dispatching = {
                effects { (this as? ParentEffect.ToChild)?.childEvent }
            },
            receiving = {
                states { ParentEvent.ChildUpdated(this) }
            }
        ).start()

        combined.effects { /*Ignore*/ }

        val values = mutableListOf<ParentState>()
        parent.states(values::add)

        parent.accept(ParentEvent.Plain)

        assertEquals(
            mutableListOf(
                ParentState(value = 0, childValue = 0),
                ParentState(value = 10, childValue = 0),
                ParentState(value = 10, childValue = 100),
            ),
            values,
        )

        // start observing effects later, simulating effects observing in onResume
        val parentEffects = mutableListOf<ParentEffect>()
        parent.effects(parentEffects::add)

        assertEquals(
            mutableListOf(
                ParentEffect.ToParent,
                ParentEffect.ToChild(ChildEvent.First),
            ),
            parentEffects
        )
    }

    private fun parentStore(
        state: ParentState,
        reducer: StateReducer<ParentEvent, ParentState, ParentEffect, ParentCommand> = NoOpReducer(),
        actor: DefaultActor<ParentCommand, ParentEvent> = NoOpActor()
    ): Store<ParentEvent, ParentEffect, ParentState> = ElmStore(state, reducer, actor)

    private fun childStore(
        state: ChildState,
        reducer: StateReducer<ChildEvent, ChildState, ChildEffect, ChildCommand> = NoOpReducer(),
        actor: DefaultActor<ChildCommand, ChildEvent> = NoOpActor()
    ): Store<ChildEvent, ChildEffect, ChildState> = ElmStore(state, reducer, actor)
}
