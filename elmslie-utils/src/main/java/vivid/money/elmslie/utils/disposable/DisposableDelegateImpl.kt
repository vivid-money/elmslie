package vivid.money.elmslie.utils.disposable

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DisposableDelegateImpl : DisposableDelegate {

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun isDisposed(): Boolean = disposables.isDisposed

    override fun clearDisposables() {
        disposables.clear()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun Disposable.bind() {
        disposables.add(this)
    }
}