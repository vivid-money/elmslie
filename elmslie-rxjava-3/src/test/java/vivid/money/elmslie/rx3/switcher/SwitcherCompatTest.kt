package vivid.money.elmslie.rx3.switcher

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.test.TestSchedulerExtension
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension

/**
 * Area for improvement: assert negative scenarios. There's no reason to test other methods since
 * they're very similar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class SwitcherCompatTest {

    private val scheduler = TestScheduler()

    @JvmField @RegisterExtension val extension = TestSchedulerExtension(scheduler)

    @JvmField @RegisterExtension val testDispatcherExtension = TestDispatcherExtension()

    object Event

    object Event2

    @Test
    fun `Switcher executes immediate action`() = runTest {
        val switcher = Switcher()

        val observer = switcher.observable(delayMillis = 0) { Observable.just(Event) }.test()

        advanceUntilIdle()
        scheduler.triggerActions()

        observer.assertResult(Event)
    }

    @Test
    fun `Switcher cancels previous request`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher
                .observable(delayMillis = 0) { Observable.timer(2, TimeUnit.SECONDS).map { Event } }
                .test()

        scheduler.triggerActions()
        runCurrent()

        val secondObserver = switcher.observable(delayMillis = 0) { Observable.just(Event2) }.test()
        runCurrent()
        scheduler.triggerActions()

        secondObserver.assertResult(Event2)
        firstObserver.assertResult()
    }

    @Test
    fun `Switcher executes sequential requests`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher
                .observable(delayMillis = 0) { Observable.timer(2, TimeUnit.SECONDS).map { Event } }
                .test()

        runCurrent()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        advanceTimeBy(2000L)

        val secondObserver =
            switcher
                .observable(delayMillis = 0) { Observable.timer(2, TimeUnit.SECONDS).map { Event } }
                .test()

        runCurrent()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        advanceTimeBy(2000L)

        firstObserver.assertResult(Event)
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels delayed request`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher.observable(delayMillis = 1000L) { Observable.just(Event) }.test()

        val secondObserver = switcher.observable(delayMillis = 0L) { Observable.just(Event) }.test()

        advanceUntilIdle()
        scheduler.triggerActions()

        firstObserver.assertResult()
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels pending requests`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher
                .observable(delayMillis = 0) { Observable.timer(2, TimeUnit.SECONDS).map { Event } }
                .test()

        val secondObserver =
            switcher
                .observable(delayMillis = 0) { Observable.timer(2, TimeUnit.SECONDS).map { Event } }
                .test()

        val thirdObserver = switcher.observable(delayMillis = 0) { Observable.just(Event) }.test()

        advanceUntilIdle()
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        firstObserver.assertResult()
        secondObserver.assertResult()
        thirdObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels consecutive requests`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher.observable(delayMillis = 300L) { Observable.just(Event) }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val secondObserver =
            switcher.observable(delayMillis = 300L) { Observable.just(Event) }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val thirdObserver =
            switcher.observable(delayMillis = 300L) { Observable.just(Event) }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val fourthObserver =
            switcher.observable(delayMillis = 300L) { Observable.just(Event) }.test()

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS)
        advanceUntilIdle()

        firstObserver.assertResult()
        secondObserver.assertResult()
        thirdObserver.assertResult()
        fourthObserver.assertResult(Event)
    }
}
