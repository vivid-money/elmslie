package vivid.money.elmslie.samples.android.loader.elm

data class State(
    val isLoading: Boolean = false,
    val value: Int? = null
)

sealed class Event {

    sealed class Ui : Event() {
        object Init : Ui()
        object ClickReload : Ui()
    }

    sealed class Internal : Event() {
        data class ValueLoaded(val value: Int) : Internal()
        object ErrorLoadingValue : Internal()
    }
}

sealed class Command {
    object LoadNewValue : Command()
}

sealed class Effect {
    object ShowError : Effect()
}

