package ${presentation_package_name}

import vivid.money.elmslie.core.store.ElmStore
import javax.inject.Inject

internal class ${domain_name}StoreFactory @Inject constructor(
    private val actor: ${domain_name}Actor
) {

    fun create(): ${domain_name}Store {
        return ElmStore(
            initialState = ${domain_name}State(),
            reducer = ${domain_name}Reducer,
            actor = actor
        ).start()
    }
}