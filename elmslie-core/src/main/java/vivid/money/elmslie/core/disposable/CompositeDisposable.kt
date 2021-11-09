package vivid.money.elmslie.core.disposable

/**
 * A convenient holder for multiple [Disposable]s
 */
class CompositeDisposable {

    private var isDisposed = false
    private val disposables = mutableListOf<Disposable>()

    operator fun plusAssign(disposable: Disposable) = add(disposable)

    fun add(disposable: Disposable) = synchronized(this) {
        if (!isDisposed) disposables += disposable
    }

    fun addAll(vararg disposables: Disposable) = disposables.forEach(::add)

    fun clear() = synchronized(this) {
        disposables.forEach { it.dispose() }
        disposables.clear()
    }
}
