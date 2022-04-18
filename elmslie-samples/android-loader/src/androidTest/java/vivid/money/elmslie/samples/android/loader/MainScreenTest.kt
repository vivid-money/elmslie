package vivid.money.elmslie.samples.android.loader

import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import vivid.money.elmslie.samples.android.loader.repository.ValueRepository
import vivid.money.elmslie.samples.android.loader.scenario.ErrorStateScenario
import vivid.money.elmslie.samples.android.loader.scenario.LoadingScreenScenario
import vivid.money.elmslie.samples.android.loader.scenario.NumberStateScenario
import java.util.concurrent.TimeUnit

class MainScreenTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private val scheduler = TestScheduler()

    @Before
    fun mockScheduler() {
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }

        activityTestRule.launchActivity(null)
    }

    @After
    fun resetSchedulers() = RxJavaPlugins.reset()

    @Test
    fun appStartsWithLoadingScreen() = run {
        scenario(LoadingScreenScenario())
    }

    @Test
    fun errorStateIsDisplayedAfterError() = run {
        ValueRepository.predefineError()

        scenario(LoadingScreenScenario())

        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)

        scenario(ErrorStateScenario())
    }

    @Test
    fun reloadingAfterErrorMayShowSomeNumber() = run {
        ValueRepository.predefineError()

        scenario(LoadingScreenScenario())
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        scenario(ErrorStateScenario())

        MainScreen.reload.click()
        scenario(LoadingScreenScenario())
    }

    @Test
    fun numberIsDisplayingAfterLoading() = run {
        ValueRepository.predefineNumber(10)

        scenario(LoadingScreenScenario())
        scheduler.advanceTimeBy(5, TimeUnit.SECONDS)
        scenario(NumberStateScenario(10))
    }
}
