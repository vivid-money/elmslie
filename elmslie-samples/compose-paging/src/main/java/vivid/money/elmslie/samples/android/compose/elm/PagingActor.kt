package vivid.money.elmslie.samples.android.compose.elm

import vivid.money.elmslie.core.store.Actor
import vivid.money.elmslie.core.switcher.Switcher
import vivid.money.elmslie.samples.android.compose.elm.PagingEvent.Internal
import vivid.money.elmslie.samples.android.compose.repository.PagingRepository

class PagingActor(
    private val repository: PagingRepository
) : Actor<PagingCommand, PagingEvent> {

    private val switcher = Switcher()

    override fun execute(command: PagingCommand) = when (command) {
        is PagingCommand.LoadPage -> switcher
            .completable { repository.loadPage(command.pageKey) }
            .mapEvents(Internal.LoadSuccess, Internal::LoadError)
        is PagingCommand.RefreshAllPages -> switcher
            .completable { repository.refreshPages() }
            .mapEvents(Internal.RefreshSuccess, Internal::RefreshError)
        is PagingCommand.ObserveAllPages -> repository.observePages()
            .mapSuccessEvent(Internal::ObserveSuccess)
    }
}