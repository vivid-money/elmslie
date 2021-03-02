package vivid.money.elmslie.core.store

import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.jupiter.api.Test

class EffectsBufferTest {

    @Test
    fun testBufferQueue() {

        val dataObservable = PublishSubject.create<Int>()

        val buffer = EffectsBuffer(dataObservable).apply { init() }.getBufferedObservable()

        val testObserver = buffer.test()

        // emitting items while subscribed
        with(dataObservable) {
            onNext(1)
            onNext(2)
        }

        testObserver.assertValues(1, 2)
        testObserver.cancel()

        // emitting items while detached
        with(dataObservable) {
            onNext(3)
            onNext(4)
        }

        val anotherObserver = buffer.test()
        anotherObserver.assertValues(3, 4)

        // emitting item when attached with having items in buffer
        with(dataObservable) {
            onNext(5)
            onComplete()
        }
        anotherObserver.assertValues(3, 4, 5)
    }

    @Test
    fun `The first subscriber receives buffered values`() {
        val dataObservable = PublishSubject.create<Int>()

        val buffer = EffectsBuffer(dataObservable).apply { init() }.getBufferedObservable()

        // Send data before the first observer
        with(dataObservable) {
            onNext(1)
            onNext(2)
        }

        val testObserver = buffer.test()
        testObserver.assertValues(1, 2)

        // Send data to the observer
        with(dataObservable) {
            onNext(3)
            onNext(4)
        }

        testObserver.assertValues(1, 2, 3, 4)

        // Send data after observer is unsubscribed
        testObserver.cancel()

        with(dataObservable) {
            onNext(5)
            onNext(6)
        }

        testObserver.assertValues(1, 2, 3, 4)
    }
}