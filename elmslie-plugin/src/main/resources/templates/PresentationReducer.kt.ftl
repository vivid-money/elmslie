package ${presentation_package_name}

import vivid.money.elmslie.core.store.Result
<#if split_events>
import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer
<#else>
import vivid.money.elmslie.core.store.dsl_reducer.DslReducer
</#if>
import ${presentation_package_name}.${domain_name}Command.*
<#if split_events>
import ${presentation_package_name}.${domain_name}Event.Internal
import ${presentation_package_name}.${domain_name}Event.Ui
<#else>
import ${presentation_package_name}.${domain_name}Event.*
</#if>

internal object ${domain_name}Reducer : <#if split_events>ScreenDslReducer<#else>DslReducer</#if><${domain_name}Event, <#if split_events>Ui, Internal, </#if>${domain_name}State,
        ${domain_name}Effect, ${domain_name}Command>(<#if split_events>Ui::class, Internal::class</#if>) {

<#if split_events>
    override fun Result.internal(event: Internal) = when (event) {
        else -> TODO("Not yet implemented")
    }

    override fun Result.ui(event: Ui) = when (event) {
        else -> TODO("Not yet implemented")
    }
<#else>
    override fun Result.reduce(event: ${domain_name}Event) = when (event) {
        else -> TODO("Not yet implemented")
    }
</#if>
}