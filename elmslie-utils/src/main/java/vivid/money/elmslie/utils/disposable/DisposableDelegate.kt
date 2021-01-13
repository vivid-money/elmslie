package vivid.money.elmslie.utils.disposable

import io.reactivex.disposables.Disposable

interface DisposableDelegate : Disposable {

    fun Disposable.bind()
    fun clearDisposables()
}
