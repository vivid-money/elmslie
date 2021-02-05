package ${presentation_package_name}

import vivid.money.elmslie.core.store.Store

internal typealias ${domain_name}Store =
        Store<${domain_name}Event, ${domain_name}Effect, ${domain_name}State>

internal data class ${domain_name}State(
    val isRefreshing: Boolean = false,
    val isError: Boolean = false
)

internal sealed class ${domain_name}Effect {
    // your code
}

internal sealed class ${domain_name}Command {
    // your code
}

internal sealed class ${domain_name}Event {
    sealed class Internal : ${domain_name}Event() {
        // your code
    }

    sealed class Ui : ${domain_name}Event() {
        object System {
            object Init : Ui()
        }

        object Click {
            // your code
        }

        object Action {
            // your code
        }
    }
}