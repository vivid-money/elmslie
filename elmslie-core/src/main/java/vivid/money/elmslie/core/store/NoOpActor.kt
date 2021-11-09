package vivid.money.elmslie.core.store

import vivid.money.elmslie.core.disposable.Disposable

/**
 * Actor that doesn't emit any events after receiving a command
 */
class NoOpActor<Command : Any, Event : Any> : DefaultActor<Command, Event> {
    override fun execute(
        command: Command,
        onEvent: (Event) -> Unit,
        onError: (Throwable) -> Unit
    ) = Disposable {}
}
