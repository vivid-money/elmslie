package money.vivid.elmslie.core.store.dsl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private object BasicDslReducer : DslReducer<TestEvent, TestState, TestEffect, TestCommand>() {

    override fun Result.reduce(event: TestEvent) =
        when (event) {
            is TestEvent.One -> {
                state { copy(one = 1) }
                state { copy(two = 2) }
            }
            is TestEvent.Two -> effects { +TestEffect.One }
            is TestEvent.Three ->
                commands {
                    +TestCommand.Two
                    +TestCommand.One
                }
            is TestEvent.Four ->
                if (event.flag) {
                    state { copy(one = 1) }
                    commands { +TestCommand.One }
                    effects { +TestEffect.One }
                } else {
                    state { copy(one = state.two, two = state.one) }
                    effects { +TestEffect.One }
                }
            is TestEvent.Five -> applyDiff()
            is TestEvent.Six -> {
                commands { +TestCommand.One.takeIf { event.flag } }
            }
        }

    // Result editing can be done in a separate function
    private fun Result.applyDiff() {
        state { copy(one = 0) }
        state { copy(one = initialState.one + 3) }
    }
}

internal class DslReducerTest {

    private val reducer = BasicDslReducer

    @Test
    fun `Multiple state updates are executed`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.One, initialState)
        assertEquals(state, TestState(one = 1, two = 2))
        assertTrue(effects.isEmpty())
        assertTrue(commands.isEmpty())
    }

    @Test
    fun `Effect is added`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Two, initialState)
        assertEquals(state, initialState)
        assertEquals(effects, listOf(TestEffect.One))
        assertTrue(commands.isEmpty())
    }

    @Test
    fun `Multiple commands are added`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Three, initialState)
        assertEquals(state, initialState)
        assertTrue(effects.isEmpty())
        assertEquals(commands, listOf(TestCommand.Two, TestCommand.One))
    }

    @Test
    fun `Complex operation`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Four(true), initialState)
        assertEquals(state, TestState(one = 1, two = 0))
        assertEquals(effects, listOf(TestEffect.One))
        assertEquals(commands, listOf(TestCommand.One))
    }

    @Test
    fun `Condition switches state values`() {
        val initialState = TestState(one = 1, two = 2)
        val (state, effects, commands) = reducer.reduce(TestEvent.Four(false), initialState)
        assertEquals(state, TestState(one = 2, two = 1))
        assertEquals(effects, listOf(TestEffect.One))
        assertTrue(commands.isEmpty())
    }

    @Test
    fun `Can access initial state`() {
        val initialState = TestState(one = 1, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Five, initialState)
        assertEquals(state, TestState(one = 4, two = 0))
        assertTrue(effects.isEmpty())
        assertTrue(commands.isEmpty())
    }

    @Test
    fun `Add command conditionally`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Six(true), initialState)
        assertEquals(state, initialState)
        assertTrue(effects.isEmpty())
        assertEquals(commands, listOf(TestCommand.One))
    }

    @Test
    fun `Skip command conditionally`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Six(false), initialState)
        assertEquals(state, initialState)
        assertTrue(effects.isEmpty())
        assertTrue(commands.isEmpty())
    }
}
