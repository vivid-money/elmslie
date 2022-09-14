package vivid.money.elmslie.rx2.switcher

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.rx2.asFlow
import vivid.money.elmslie.core.ElmScope
import vivid.money.elmslie.core.switcher.Switcher

/**
 * Cancels all scheduled actions after [delayMillis] pass.
 *
 * @param delayMillis Cancellation delay measured with milliseconds.
 */
fun <Event : Any> Switcher.cancel(delayMillis: Long = 0) =
    observable(delayMillis) { Observable.empty() }

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
): Observable<Event> =
    Observable.create { emitter ->
        val job =
            action()
                .asFlow()
                .switchInternal(
                    coroutineScope = ElmScope("Switcher"),
                    delayMillis = delayMillis,
                    onEach = { emitter.onNext(it) },
                    onError = { emitter.onError(it) },
                    onComplete = { emitter.onComplete() },
                )

        job.invokeOnCompletion { emitter.onComplete() }
        emitter.setCancellable { clear(job) }
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
