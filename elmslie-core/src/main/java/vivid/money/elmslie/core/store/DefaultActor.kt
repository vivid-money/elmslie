package vivid.money.elmslie.core.store

import vivid.money.elmslie.core.disposable.Disposable

fun interface DefaultActor<Command : Any, Event : Any> {

    /**
     * Executes a command. This method is always called in ElmslieConfig.backgroundExecutor.
     * Usually background thread.
     */
    fun execute(
        command: Command,
        onEvent: (Event) -> Unit,
        onError: (Throwable) -> Unit
    ): Disposable
}
