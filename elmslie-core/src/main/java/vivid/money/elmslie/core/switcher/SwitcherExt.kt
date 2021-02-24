@file:JvmName("SwitcherExt")

package vivid.money.elmslie.core.switcher

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Executes [action] and cancels all previous requests scheduled on the same [switcher]
 *
 * @param switcher Switcher which will execute the request
 * @param delayMillis Operation delay in milliseconds. Can be used to debounce requests
 * @param action Operation to be executed
 */
fun <Event> switchOn(
    switcher: Switcher,
    delayMillis: Long = 0,
    action: () -> Observable<Event>
): Observable<Event> {
    return switcher.switch(delayMillis, action)
}

/**
 * Same as [switchOn], but for [Single]
 */
fun <Event> switchOnSingle(
    switcher: Switcher,
    delayMillis: Long = 0,
    action: () -> Single<Event>
): Observable<Event> {
    return switchOn(switcher, delayMillis) { action().toObservable() }
}

/**
 * Same as [switchOn], but for [Completable]
 */
fun <Event> switchOnCompletable(
    switcher: Switcher,
    delayMillis: Long = 0,
    action: () -> Completable
): Observable<Event> {
    return switchOn(switcher, delayMillis) { action().toObservable<Event>() }
}
