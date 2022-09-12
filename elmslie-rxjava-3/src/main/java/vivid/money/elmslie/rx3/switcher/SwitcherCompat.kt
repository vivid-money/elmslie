package vivid.money.elmslie.rx3.switcher

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.core.switcher.Switcher

/**
 * Cancels all scheduled actions after [delayMillis] pass.
 *
 * @param delayMillis Cancellation delay measured with milliseconds.
 */
fun Switcher.cancel(delayMillis: Long = 0) = observable(delayMillis) { Observable.empty() }

/**
 * Executes [action] and cancels all previous requests scheduled on this [Switcher]
 *
 * @param delayMillis Operation delay in milliseconds. Can be used to debounce requests
 * @param action Operation to be executed
 */
fun <Event : Any> Switcher.observable(
    delayMillis: Long = 0,
    action: () -> Observable<Event>,
): Observable<Event> =
    Observable.create { emitter ->
        val job =
            switchInternal(delayMillis) {
                action()
                    .doOnComplete(emitter::onComplete)
                    .subscribe(emitter::onNext, emitter::onError)
            }
        emitter.setCancellable { job?.cancel() }
    }

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

@Deprecated("Please, use property methods", ReplaceWith("observable(delayMillis, action)"))
fun <Event : Any> Switcher.switch(
    delayMillis: Long = 0,
    action: () -> Observable<Event>,
) = observable(delayMillis, action)

@Deprecated("Please use instance methods", ReplaceWith("switcher.observable(delayMillis, action)"))
fun <Event : Any> switchOn(
    switcher: Switcher,
    delayMillis: Long = 0,
    action: () -> Observable<Event>
) = switcher.observable(delayMillis, action)
