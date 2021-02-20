package vivid.money.elmslie.core.store.dsl_reducer

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

private object BasicDslReducer : DslReducer<TestEvent, TestState, TestEffect, TestCommand>() {

    override fun Result.reduce(event: TestEvent) = when (event) {
        is TestEvent.One -> {
            state { copy(one = 1) }
            state { copy(two = 2) }
        }
        is TestEvent.Two -> effects { +TestEffect.One }
        is TestEvent.Three -> commands {
            +TestCommand.Two
            +TestCommand.One
        }
        is TestEvent.Four -> if (event.flag) {
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
        state shouldBe TestState(one = 1, two = 2)
        effects.shouldBeEmpty()
        commands.shouldBeEmpty()
    }

    @Test
    fun `Effect is added`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Two, initialState)
        state shouldBe initialState
        effects shouldBe listOf(TestEffect.One)
        commands.shouldBeEmpty()
    }

    @Test
    fun `Multiple commands are added`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Three, initialState)
        state shouldBe initialState
        effects.shouldBeEmpty()
        commands shouldBe listOf(TestCommand.Two, TestCommand.One)
    }

    @Test
    fun `Complex operation`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Four(true), initialState)
        state shouldBe TestState(one = 1, two = 0)
        effects shouldBe listOf(TestEffect.One)
        commands shouldBe listOf(TestCommand.One)
    }

    @Test
    fun `Condition switches state values`() {
        val initialState = TestState(one = 1, two = 2)
        val (state, effects, commands) = reducer.reduce(TestEvent.Four(false), initialState)
        state shouldBe TestState(one = 2, two = 1)
        effects shouldBe listOf(TestEffect.One)
        commands.shouldBeEmpty()
    }

    @Test
    fun `Can access initial state`() {
        val initialState = TestState(one = 1, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Five, initialState)
        state shouldBe TestState(one = 4, two = 0)
        effects.shouldBeEmpty()
        commands.shouldBeEmpty()
    }

    @Test
    fun `Add command conditionally`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Six(true), initialState)
        state shouldBe initialState
        effects.shouldBeEmpty()
        commands shouldBe listOf(TestCommand.One)
    }

    @Test
    fun `Skip command conditionally`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestEvent.Six(false), initialState)
        state shouldBe initialState
        effects.shouldBeEmpty()
        commands.shouldBeEmpty()
    }
}
