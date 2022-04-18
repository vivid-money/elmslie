package vivid.money.elmslie.samples.android.loader.scenario

import com.kaspersky.kaspresso.testcases.api.scenario.BaseScenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import vivid.money.elmslie.samples.android.loader.MainScreen

class LoadingScreenScenario : BaseScenario<Unit>() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("LoadingScreen is displayed") {
            MainScreen {
                currentValue.hasText("Loading...")
            }
        }
    }
}