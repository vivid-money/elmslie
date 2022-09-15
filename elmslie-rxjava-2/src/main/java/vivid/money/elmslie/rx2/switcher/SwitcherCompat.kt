package vivid.money.elmslie.rx2.switcher

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxObservable
import vivid.money.elmslie.core.switcher.Switcher

/**
 * Cancels all scheduled actions after [delayMillis] pass.
 *
 * @param delayMillis Cancellation delay measured with milliseconds.
 */
fun Switcher.cancel(delayMillis: Long = 0): Observable<Any> = rxObservable {
    cancelInternal(delayMillis)
}

/**
 * Executes an [action] and cancels all previous requests scheduled for this [Switcher].
 *
 * @param delayMillis Operation delay measured with milliseconds.
 * ```
 *                    Can be specified to debounce requests.
 * @param action
 * ```
 * Operation to be executed.
 */
fun <Event : Any> Switcher.observable(
    delayMillis: Long = 0,
    action: () -> Observable<Event>,
): Observable<Event> = switchInternal(delayMillis = delayMillis) { action.invoke().asFlow() }.asObservable()

/** Same as [observable], but for [Single]. */
fun <Event : Any> Switcher.single(
    delayMillis: Long = 0,
    action: () -> Single<Event>,
): Single<Event> = observable(delayMillis) { action().toObservable() }.firstOrError()

/** Same as [observable], but for [Maybe]. */
fun <Event : Any> Switcher.maybe(
    delayMillis: Long = 0,
    action: () -> Maybe<Event>,
): Maybe<Event> = observable(delayMillis) { action().toObservable() }.firstElement()

/** Same as [observable], but for [Completable]. */
fun Switcher.completable(
    delayMillis: Long = 0,
    action: () -> Completable,
): Completable = observable(delayMillis) { action().toObservable() }.ignoreElements()
