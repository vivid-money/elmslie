package vivid.money.elmslie.samples.android.loader.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import vivid.money.elmslie.samples.android.loader.MainScreen

class NumberStateScenario(
    private val number: Int
) : BaseScenario<Unit>() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Number is displayed") {
            MainScreen {
                currentValue.hasText("Value = $number")
            }
        }
    }
}