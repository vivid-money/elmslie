package ${presentation_package_name}

internal data class ${domain_name}State(
    // your code
)

internal sealed class ${domain_name}Effect {
    // your code
}

internal sealed class ${domain_name}Command {
    // your code
}

internal sealed class ${domain_name}Event {
    <#if split_events>
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
    <#else>
    // your code
    </#if>
}