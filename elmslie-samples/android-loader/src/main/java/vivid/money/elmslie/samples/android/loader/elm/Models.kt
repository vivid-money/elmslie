package vivid.money.elmslie.samples.android.loader.elm

data class State(
    val isLoading: Boolean = false,
    val currentValue: Int? = null
)

sealed class Event {
    object Init : Event()

    object ClickReload : Event()
    data class ValueLoaded(val value: Int) : Event()
    object ErrorLoadingValue : Event()
}

sealed class Command {
    object LoadNewValue : Command()
}

sealed class Effect {
    object ShowError : Effect()
}

