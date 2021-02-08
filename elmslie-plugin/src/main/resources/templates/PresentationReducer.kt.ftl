package ${presentation_package_name}

import vivid.money.elmslie.core.store.Result
import vivid.money.elmslie.core.store.StateReducer
import ${presentation_package_name}.${domain_name}Command.*
<#if split_events>
import ${presentation_package_name}.${domain_name}Event.Internal
import ${presentation_package_name}.${domain_name}Event.Ui
<#else>
import ${presentation_package_name}.${domain_name}Event.*
</#if>

internal object ${domain_name}Reducer : StateReducer<${domain_name}Event, ${domain_name}State,
        ${domain_name}Effect, ${domain_name}Command> {

    override fun reduce(
        event: ${domain_name}Event,
        state: ${domain_name}State
    ): Result<${domain_name}State, ${domain_name}Effect, ${domain_name}Command> =
        when (event) {
            <#if split_events>
            is Internal -> handleInternalEvent(event, state)
            is Ui -> handleUiEvent(event, state)
            <#else>
            // your code
            </#if>
        }

    <#if split_events>
    private fun handleInternalEvent(
        event: Internal,
        state: ${domain_name}State
    ): Result<${domain_name}State, ${domain_name}Effect, ${domain_name}Command> =
        when (event) {
            // your code
        }

    private fun handleUiEvent(
        event: Ui,
        state: ${domain_name}State
    ): Result<${domain_name}State, ${domain_name}Effect, ${domain_name}Command> =
        when (event) {
            is Ui.System.Init -> Result(state)
            // your code
        }
    </#if>
}