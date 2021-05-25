package vivid.money.elmslie.samples.android.compose.elm

import vivid.money.elmslie.core.store.dsl_reducer.ScreenDslReducer
import vivid.money.elmslie.samples.android.compose.elm.PagingEvent.Internal
import vivid.money.elmslie.samples.android.compose.elm.PagingEvent.Ui

object PagingReducer : ScreenDslReducer<PagingEvent, Ui, Internal, PagingState,
        PagingEffect, PagingCommand>(Ui::class, Internal::class) {

    override fun Result.ui(event: Ui) = when (event) {
        Ui.Init -> commands {
            +PagingCommand.ObserveAllPages
            +PagingCommand.LoadPage(pageKey = null)
        }
        Ui.ClickReloadScreen -> {
            state { copy(error = null) }
            commands { +PagingCommand.LoadPage(pageKey = null) }
        }
        Ui.ClickReloadPage -> {
            state { copy(error = null) }
            commands { +PagingCommand.LoadPage(pageKey = state.nextPageKey) }
        }
        Ui.CloseToEnd -> commands {
            +PagingCommand.LoadPage(state.nextPageKey).takeIf { state.canLoadMore }
        }
        Ui.PullToRefresh -> {
            state { copy(isRefreshing = true) }
            commands { +PagingCommand.RefreshAllPages }
        }
    }

    override fun Result.internal(event: Internal) = when (event) {
        is Internal.ObserveSuccess -> state { copy(pages = event.pages, isRefreshing = false) }
        is Internal.LoadSuccess -> state { copy(isRefreshing = false) }
        is Internal.LoadError -> {
            state { copy(error = event.error, isRefreshing = false) }
            effects { +PagingEffect.LoadError(event.error) }
        }
        is Internal.RefreshSuccess -> state { copy(isRefreshing = false) }
        is Internal.RefreshError -> {
            state { copy(error = event.error, isRefreshing = false) }
            effects { +PagingEffect.RefreshError(event.error) }
        }
    }
}