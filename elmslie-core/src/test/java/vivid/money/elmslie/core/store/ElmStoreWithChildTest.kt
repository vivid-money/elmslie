package vivid.money.elmslie.core.store

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
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
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension

@OptIn(ExperimentalCoroutinesApi::class)
class ElmStoreWithChildTest {

    @JvmField @RegisterExtension val testDispatcherExtension = TestDispatcherExtension()

    @Test
    fun `Parent event is propagated to child and state update is received afterwards`() = runTest {
        val parent =
            parentStore(
                state = ParentState(),
                reducer = { event, state ->
                    when (event) {
                        is ParentEvent.Plain ->
                            Result(
                                state = state.copy(value = 10),
                                effect = ParentEffect.ToChild(ChildEvent.First)
                            )
                        is ParentEvent.ChildUpdated ->
                            Result(state = state.copy(childValue = event.state.value))
                    }
                },
            )
        val child =
            childStore(
                state = ChildState(),
                reducer = { _, state -> Result(state.copy(value = 100), effect = ChildEffect) },
            )
        val coordination =
            parent.coordinates(
                responder = child,
                dispatching = { states { ChildEvent.First } },
                receiving = {
                    states { ParentEvent.ChildUpdated(this) }
                    effects { ParentEvent.Plain }
                }
            )

        val values = mutableListOf<ParentState>()
        val collectJob = launch { parent.states().toList(values) }
        coordination.start()
        parent.accept(ParentEvent.Plain)
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                ParentState(0, 0),
                ParentState(10, 0),
                ParentState(10, 100),
            ),
            values
        )
        collectJob.cancel()
    }

    @Test
    fun `Parent effect is propagated to child and effect is received afterwards`() = runTest {
        val parent =
            parentStore(
                ParentState(),
                { _, state -> Result(state, effect = ParentEffect.ToParent) }
            )
        val child =
            childStore(
                ChildState(),
                { _, state -> Result(state.copy(value = 100), effect = ChildEffect) }
            )
        parent.coordinates(child, dispatching = { effects { ChildEvent.First } }).start()

        val values = mutableListOf<ChildEffect>()
        val collectJob = launch { child.effects().toList(values) }
        parent.accept(ParentEvent.Plain)
        advanceUntilIdle()

        assertEquals(
            mutableListOf(ChildEffect),
            values,
        )
        collectJob.cancel()
    }

    @Test
    fun `Child state update after action is propagated to the parent`() = runTest {
        val parent =
            parentStore(
                ParentState(),
                { event, state ->
                    if (event is ParentEvent.ChildUpdated) {
                        Result(state = state.copy(childValue = event.state.value))
                    } else {
                        Result(state = state.copy(value = 10), effect = ParentEffect.ToParent)
                    }
                }
            )
        val child =
            childStore(
                ChildState(),
                { event, state ->
                    if (event == ChildEvent.First) {
                        Result(state.copy(value = 100), command = ChildCommand)
                    } else {
                        Result(state.copy(value = 200))
                    }
                },
                { flowOf(ChildEvent.Second) }
            )
        parent
            .coordinates(
                child,
                dispatching = { effects { ChildEvent.First } },
                receiving = { states { ParentEvent.ChildUpdated(this) } }
            )
            .start()

        val values = mutableListOf<ParentState>()
        val collectJob = launch { parent.states().toList(values) }
        parent.accept(ParentEvent.Plain)
        advanceUntilIdle()

        assertEquals(
            mutableListOf(
                ParentState(0, 0),
                ParentState(10, 0),
                ParentState(10, 100),
                ParentState(10, 200)
            ),
            values,
        )

        collectJob.cancel()
    }

    @Test
    fun `Stopping the binding stops both parent and child`() = runTest {
        val parent = parentStore(ParentState())
        val child = childStore(ChildState())
        val binding = parent.coordinates(child).start()
        binding.stop()
        advanceUntilIdle()

        assert(!parent.isStarted)
        assert(!child.isStarted)
    }

    @Test
    fun `Child command results are received by parents consecutively`() = runTest {
        val parent =
            parentStore(
                ParentState(),
                { event, state ->
                    if (event is ParentEvent.ChildUpdated) {
                        Result(state = state.copy(childValue = event.state.value))
                    } else {
                        Result(state = state, effect = ParentEffect.ToParent)
                    }
                }
            )
        val child =
            childStore(
                state = ChildState(),
                reducer = { event, state ->
                    when (event) {
                        ChildEvent.First -> Result(state.copy(value = 100), command = ChildCommand)
                        ChildEvent.Second -> Result(state.copy(value = 200))
                        ChildEvent.Third -> Result(state.copy(value = 300))
                    }
                },
                actor = {
                    flow {
                        emit(ChildEvent.Second)
                        emit(ChildEvent.Third)
                    }
                }
            )

        parent
            .coordinates(
                child,
                dispatching = { effects { ChildEvent.First } },
                receiving = { states { ParentEvent.ChildUpdated(this) } }
            )
            .start()

        val values = mutableListOf<ParentState>()
        val collectJob = launch { parent.states().toList(values) }
        parent.accept(ParentEvent.Plain)
        advanceUntilIdle()

        assertEquals(
            mutableListOf(ParentState(0, 0), ParentState(0, 100), ParentState(0, 300)),
            values,
        )
        collectJob.cancel()
    }

    private fun parentStore(
        state: ParentState,
        reducer: StateReducer<ParentEvent, ParentState, ParentEffect, ParentCommand> =
            NoOpReducer(),
        actor: DefaultActor<ParentCommand, ParentEvent> = NoOpActor()
    ): Store<ParentEvent, ParentEffect, ParentState> = ElmStore(state, reducer, actor)

    private fun childStore(
        state: ChildState,
        reducer: StateReducer<ChildEvent, ChildState, ChildEffect, ChildCommand> = NoOpReducer(),
        actor: DefaultActor<ChildCommand, ChildEvent> = NoOpActor()
    ): Store<ChildEvent, ChildEffect, ChildState> = ElmStore(state, reducer, actor)
}
