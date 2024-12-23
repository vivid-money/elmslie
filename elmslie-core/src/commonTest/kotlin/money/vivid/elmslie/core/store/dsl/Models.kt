package money.vivid.elmslie.core.store.dsl

data class TestState(val one: Int, val two: Int)

sealed class TestScreenEvent {

  sealed class Ui : TestScreenEvent() {
    object One : Ui()
  }

  sealed class Internal : TestScreenEvent() {
    object One : Internal()
  }
}

sealed class TestEvent {
  object One : TestEvent()

  object Two : TestEvent()

  object Three : TestEvent()

  data class Four(val flag: Boolean) : TestEvent()

  object Five : TestEvent()

  data class Six(val flag: Boolean) : TestEvent()
}

sealed class TestEffect {
  object One : TestEffect()
}

sealed class TestCommand {
  object One : TestCommand()

  object Two : TestCommand()
}
