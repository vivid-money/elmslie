package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * Buffers a [PublishSubject], if it's guaranteed that it has a single subscriber
 */
internal class EffectsBuffer<T>(
    private val source: PublishSubject<T>
) : AtomicBoolean(), Disposable {

    private val disposableRef = AtomicReference<Disposable>()
    private val bufferRef = AtomicReference<Subject<T>>()

    fun init(): Disposable {
        startBuffering()
        return this
    }

    fun getBufferedObservable(): Observable<T> {
        return getBuffer() // Get buffered events
            .concatWith(source) // And then start observing the source
            .doFinally { startBuffering() } // Start buffering again to not lose events from source
            .doOnSubscribe { stopBuffering() } // Stop buffering since we're getting events from source
    }

    override fun isDisposed(): Boolean = get()

    override fun dispose() {
        set(true)
        stopBuffering()
    }

    private fun startBuffering() {
        val buffer = ReplaySubject.create<T>().toSerialized()
        bufferRef.set(buffer)
        disposableRef.set(source.subscribe { buffer.onNext(it) })
    }

    private fun stopBuffering() {
        bufferRef.get()?.onComplete()
        disposableRef.get()?.dispose()
    }

    private fun getBuffer(): Observable<T> {
        // Defer is necessary so that every subscriber would reevaluate the buffer
        return Observable.defer { bufferRef.get() ?: error("startBuffering must be called first") }
    }
}
