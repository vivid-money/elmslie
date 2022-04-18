package vivid.money.elmslie.samples.android.loader.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import vivid.money.elmslie.samples.android.loader.MainScreen

class ErrorStateScenario : BaseScenario<Unit>() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Error state is displayed") {
            MainScreen {
                snackbar.text.hasText("Error!")
                currentValue.hasText("Value = Unknown")
            }
        }
    }
}