package vivid.money.elmslie.rx3.switcher

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.test.TestSchedulerExtension
import java.util.concurrent.TimeUnit

/**
 * Area for improvement: assert negative scenarios.
 * There's no reason to test other methods since they're very similar.
 */
internal class SwitcherCompatTest {

    private val scheduler = TestScheduler()

    @JvmField
    @RegisterExtension
    val extension = TestSchedulerExtension(scheduler)

    object Event

    @Test
    fun `Switcher executes immediate action`() {
        val switcher = Switcher()

        val observer = switcher.observable(0) {
            Observable.just(Event)
        }.test()

        switcher.await()
        scheduler.triggerActions()

        observer.assertResult(Event)
    }

    @Test
    fun `Switcher cancels previous request`() {
        val switcher = Switcher()

        val firstObserver = switcher.observable(0) {
            Observable.timer(2, TimeUnit.SECONDS).map { Event }
        }.test()

        switcher.await()
        scheduler.triggerActions()

        val secondObserver = switcher.observable(0) {
            Observable.just(Event)
        }.test()

        switcher.await()
        scheduler.triggerActions()

        secondObserver.assertResult(Event)
        firstObserver.assertResult()
    }

    @Test
    fun `Switcher executes sequential requests`() {
        val switcher = Switcher()

        val firstObserver = switcher.observable(0) {
            Observable.timer(2, TimeUnit.SECONDS).map { Event }
        }.test()

        switcher.await()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val secondObserver = switcher.observable(0) {
            Observable.timer(2, TimeUnit.SECONDS).map { Event }
        }.test()

        switcher.await()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        firstObserver.assertResult(Event)
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels delayed request`() {
        val switcher = Switcher()

        val firstObserver = switcher.observable(1000L) {
            Observable.just(Event)
        }.test()

        val secondObserver = switcher.observable(0L) {
            Observable.just(Event)
        }.test()

        switcher.await()
        scheduler.triggerActions()

        firstObserver.assertValuesOnly()
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels pending requests`() {
        val switcher = Switcher()

        val firstObserver = switcher.observable(0) {
            Observable.timer(2, TimeUnit.SECONDS).map { Event }
        }.test()

        val secondObserver = switcher.observable(0) {
            Observable.timer(2, TimeUnit.SECONDS).map { Event }
        }.test()

        val thirdObserver = switcher.observable(0) {
            Observable.just(Event)
        }.test()

        switcher.await()
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        firstObserver.assertValuesOnly()
        secondObserver.assertValuesOnly()
        thirdObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels consecutive requests`() {
        val switcher = Switcher()

        val firstObserver = switcher.observable(300L) {
            Observable.just(Event)
        }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val secondObserver = switcher.observable(300L) {
            Observable.just(Event)
        }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val thirdObserver = switcher.observable(300L) {
            Observable.just(Event)
        }.test()

        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)

        val fourthObserver = switcher.observable(300L) {
            Observable.just(Event)
        }.test()

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS)
        switcher.await()

        firstObserver.assertValuesOnly()
        secondObserver.assertValuesOnly()
        thirdObserver.assertValuesOnly()
        fourthObserver.assertResult(Event)
    }
}
