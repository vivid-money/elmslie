package vivid.money.elmslie.rx3.switcher

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.test.TestSchedulerExtension
import vivid.money.elmslie.test.background.executor.TestDispatcherExtension
import kotlin.coroutines.CoroutineContext

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
    fun `Switcher execute delayed request`() = runTest {
        val switcher = Switcher()

        val firstObserver =
            switcher
                .observable(delayMillis = 100, context = this@runTest.coroutineContext) { Observable.just(Event) }
                .test()
        advanceTimeBy(150)
        scheduler.advanceTimeBy(150, TimeUnit.MILLISECONDS)

        advanceUntilIdle()
        scheduler.triggerActions()

        firstObserver.assertResult(Event)
    }
}

private fun <Event : Any> Switcher.observable(
    delayMillis: Long = 0,
    context: CoroutineContext,
    action: () -> Observable<Event>,
): Observable<Event> {
    return switch(delayMillis) { action.invoke().asFlow() }.asObservable(context)
}
