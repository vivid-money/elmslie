package ${presentation_package_name}

import vivid.money.elmslie.core.store.Actor
import ${presentation_package_name}.${domain_name}Command.*
import ${presentation_package_name}.${domain_name}Event.Internal
import io.reactivex.Observable
import javax.inject.Inject

internal class ${domain_name}Actor @Inject constructor(
    // your dependencies
) : Actor<${domain_name}Command, ${domain_name}Event> {

    override fun execute(command: ${domain_name}Command): Observable<${domain_name}Event> =
        when (command) {
            // your code
        }
}