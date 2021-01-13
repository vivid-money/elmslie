package vivid.money.elmslie.core.switcher

import vivid.money.elmslie.core.store.Actor
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Allows to execute requests in [Actor] in a switching manner.
 * Each request will cancel a previous one.
 *
 * Usage:
 * ```
 * private val switcher = Switcher<Event>()
 *
 * override fun execute(command: Command): Observable<Event> = when (command) {
 *    is MyCommand -> switchOn(switcher) {
 *        Observable.just(123)
 *    }
 * }
 * ```
 */
class Switcher {

    private val nextRequestId = AtomicInteger(0)
    private val requests = PublishSubject.create<Int>()

    fun <Event> switch(delayMillis: Long = 0, action: () -> Observable<Event>): Observable<Event> {
        val requestId = nextRequestId.getAndIncrement()
        requests.onNext(requestId)
        return Completable.timer(delayMillis, TimeUnit.MILLISECONDS)
            .andThen(action())
            .takeUntil(requests.filter { it > requestId })
    }
}
