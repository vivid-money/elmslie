package vivid.money.elmslie.core

import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

internal fun Completable.toV3() = `as`(RxJavaBridge.toV3Completable())
internal fun <T> Single<T>.toV3() = `as`(RxJavaBridge.toV3Single())
internal fun <T> Maybe<T>.toV3() = `as`(RxJavaBridge.toV3Maybe())
internal fun <T> Observable<T>.toV3() = `as`(RxJavaBridge.toV3Observable())

internal fun <T> io.reactivex.rxjava3.core.Observable<T>.toV2() = to(RxJavaBridge.toV2Observable<T>())
