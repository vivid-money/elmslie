package vivid.money.elmslie.core.store.dsl_reducer

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

object BasicScreenDslReducer :
    ScreenDslReducer<TestScreenEvent, TestScreenEvent.Ui, TestScreenEvent.Internal, TestState, TestEffect, TestCommand>(
        TestScreenEvent.Ui::class,
        TestScreenEvent.Internal::class
    ) {

    override fun Result.ui(event: TestScreenEvent.Ui) = when (event) {
        is TestScreenEvent.Ui.One -> state { copy(one = 1, two = 2) }
    }

    override fun Result.internal(event: TestScreenEvent.Internal) = when (event) {
        is TestScreenEvent.Internal.One -> commands {
            +TestCommand.One
            +TestCommand.Two
        }
    }
}

// The same code
object PlainScreenDslReducer : DslReducer<TestScreenEvent, TestState, TestEffect, TestCommand>() {

    override fun Result.reduce(event: TestScreenEvent) = when (event) {
        is TestScreenEvent.Ui -> reduce(event)
        is TestScreenEvent.Internal -> reduce(event)
    }

    private fun Result.reduce(event: TestScreenEvent.Ui) = when (event) {
        is TestScreenEvent.Ui.One -> state { copy(one = 1, two = 2) }
    }

    private fun Result.reduce(event: TestScreenEvent.Internal) = when (event) {
        is TestScreenEvent.Internal.One -> commands {
            +TestCommand.One
            +TestCommand.Two
        }
    }
}

internal class ScreenDslReducerTest {

    private val reducer = BasicScreenDslReducer

    @Test
    fun `Ui event is executed`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestScreenEvent.Ui.One, initialState)
        state shouldBe TestState(one = 1, two = 2)
        effects.shouldBeEmpty()
        commands.shouldBeEmpty()
    }

    @Test
    fun `Internal event is executed`() {
        val initialState = TestState(one = 0, two = 0)
        val (state, effects, commands) = reducer.reduce(TestScreenEvent.Internal.One, initialState)
        state shouldBe initialState
        effects.shouldBeEmpty()
        commands shouldBe listOf(TestCommand.One, TestCommand.Two)
    }
}
