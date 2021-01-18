package vivid.money.elmslie.core.switcher

import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import vivid.money.elmslie.test.TestSchedulerExtension
import java.util.concurrent.TimeUnit

class SwitcherTest {

    private val scheduler = TestScheduler()

    @JvmField
    @RegisterExtension
    val extension = TestSchedulerExtension(scheduler)

    object Event

    @Test
    fun `Switcher executes action`() {
        val switcher = Switcher()
        val observer = switcher.switch { Observable.just(Event) }
            .test()
        scheduler.triggerActions()
        observer.assertResult(Event)
    }

    @Test
    fun `Switcher cancels previous request`() {
        val switcher = Switcher()
        val observerOne = switcher.switch { Observable.timer(2, TimeUnit.SECONDS).map { Event } }.test()
        val observerTwo = switcher.switch { Observable.just(Event) }.test()
        scheduler.triggerActions()
        observerTwo.assertResult(Event)
        observerOne.assertResult()
    }

    @Test
    fun `Switcher cancels multiple pending requests`() {
        val switcher = Switcher()
        val firstObserver = switcher.switch { Observable.timer(2, TimeUnit.SECONDS).map { Event } }.test()
        val secondObserver = switcher.switch { Observable.timer(2, TimeUnit.SECONDS).map { Event } }.test()
        val thirdObserver = switcher.switch { Observable.just(Event) }.test()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        thirdObserver.assertResult(Event)
        firstObserver.assertResult()
        secondObserver.assertResult()
    }

    @Test
    fun `Switcher executes sequential requests`() {
        val switcher = Switcher()
        val firstObserver = switcher.switch { Observable.timer(2, TimeUnit.SECONDS).map { Event } }.test()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        val secondObserver = switcher.switch { Observable.timer(2, TimeUnit.SECONDS).map { Event } }.test()
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        firstObserver.assertResult(Event)
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels delayed request`() {
        val switcher = Switcher()
        val firstObserver = switcher.switch(1000L) { Observable.just(1) }.map { Event }.test()
        val secondObserver = switcher.switch(0L) { Observable.just(1) }.map { Event }.test()
        scheduler.triggerActions()
        firstObserver.assertResult()
        secondObserver.assertResult(Event)
    }

    @Test
    fun `Switcher cancels three consecutive requests`() {
        val switcher = Switcher()
        val firstObserver = switcher.switch(300L) { Observable.just(1) }.map { Event }.test()
        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)
        val secondObserver = switcher.switch(300L) { Observable.just(1) }.map { Event }.test()
        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)
        val thirdObserver = switcher.switch(300L) { Observable.just(1) }.map { Event }.test()
        scheduler.advanceTimeBy(250, TimeUnit.MILLISECONDS)
        val fourthObserver = switcher.switch(300L) { Observable.just(1) }.map { Event }.test()
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS)
        firstObserver.assertResult()
        secondObserver.assertResult()
        thirdObserver.assertResult()
        fourthObserver.assertResult(Event)
    }
}