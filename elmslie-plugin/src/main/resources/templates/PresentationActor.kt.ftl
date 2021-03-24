package ${presentation_package_name}

import vivid.money.elmslie.core.store.Actor
import ${presentation_package_name}.${domain_name}Command.*
<#if split_events>
import ${presentation_package_name}.${domain_name}Event.Internal
<#else>
import ${presentation_package_name}.${domain_name}Event.*
</#if>
import io.reactivex.Observable
import javax.inject.Inject

internal class ${domain_name}Actor @Inject constructor(
    // your dependencies
) : Actor<${domain_name}Command, ${domain_name}Event> {

    override fun execute(
        command: ${domain_name}Command
    ): Observable<<#if split_events>Internal<#else>${domain_name}Event</#if>> = when (command) {
        // your code
    }
}