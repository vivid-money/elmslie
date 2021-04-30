package vivid.money.elmslie.core.switcher

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import vivid.money.elmslie.core.store.Actor
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

    @Deprecated("Please, use property methods", ReplaceWith("observable(delayMillis, action)"))
    fun <Event : Any> switch(
        delayMillis: Long = 0,
        action: () -> Observable<Event>,
    ) = observable(delayMillis, action)

    /**
     * Executes [action] and cancels all previous requests scheduled on this [Switcher]
     *
     * @param delayMillis Operation delay in milliseconds. Can be used to debounce requests
     * @param action Operation to be executed
     */
    fun <Event : Any> observable(
        delayMillis: Long = 0,
        action: () -> Observable<Event>,
    ): Observable<Event> {
        val requestId = nextRequestId.getAndIncrement()
        requests.onNext(requestId)
        return Completable.timer(delayMillis, TimeUnit.MILLISECONDS)
            .andThen(action())
            .takeUntil(requests.filter { it > requestId })
    }

    /**
     * Same as [observable], but for [Single]
     */
    fun <Event : Any> single(
        delayMillis: Long = 0,
        action: () -> Single<Event>,
    ): Single<Event> = observable(delayMillis) { action().toObservable() }.firstOrError()

    /**
     * Same as [observable], but for [Maybe]
     */
    fun <Event : Any> maybe(
        delayMillis: Long = 0,
        action: () -> Maybe<Event>,
    ): Maybe<Event> = observable(delayMillis) { action().toObservable() }.firstElement()

    /**
     * Same as [observable], but for [Completable]
     */
    fun <Event : Any> completable(
        delayMillis: Long = 0,
        action: () -> Single<Event>,
    ): Completable = observable(delayMillis) { action().toObservable() }.ignoreElements()
}
