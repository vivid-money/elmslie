package vivid.money.elmslie.samples.android.compose.elm

import vivid.money.elmslie.samples.android.compose.model.Page

data class PagingState(
    val pages: List<Page>? = null,
    val error: Throwable? = null,
    val isRefreshing: Boolean = false
) {
    val items = pages?.flatMap { it.items }
    val nextPageKey = pages?.lastOrNull()?.nextPageKey
    val isError = error != null
    val isLoadedAllPages = nextPageKey != null
    val canLoadMore = isLoadedAllPages && !isError
    val hasMorePages = pages?.lastOrNull()?.nextPageKey != null
}

sealed class PagingEvent {

    sealed class Ui : PagingEvent() {
        object Init : Ui()

        object ClickReloadScreen : Ui()
        object ClickReloadPage : Ui()

        object CloseToEnd : Ui()
        object PullToRefresh : Ui()
    }

    sealed class Internal : PagingEvent() {

        data class ObserveSuccess(val pages: List<Page>) : Internal()

        object LoadSuccess : Internal()
        data class LoadError(val error: Throwable) : Internal()

        object RefreshSuccess : Internal()
        data class RefreshError(val error: Throwable) : Internal()
    }
}

sealed class PagingEffect {
    data class LoadError(val error: Throwable) : PagingEffect()
    data class RefreshError(val error: Throwable) : PagingEffect()
}

sealed class PagingCommand {

    data class LoadPage(val pageKey: String? = null) : PagingCommand()
    object ObserveAllPages : PagingCommand()
    object RefreshAllPages : PagingCommand()
}
