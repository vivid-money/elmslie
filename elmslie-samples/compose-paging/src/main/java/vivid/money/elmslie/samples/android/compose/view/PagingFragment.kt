package vivid.money.elmslie.samples.android.compose.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import vivid.money.elmslie.compose.ElmComponentFragment
import vivid.money.elmslie.samples.android.compose.elm.PagingEffect
import vivid.money.elmslie.samples.android.compose.elm.PagingEvent
import vivid.money.elmslie.samples.android.compose.elm.PagingEvent.Ui
import vivid.money.elmslie.samples.android.compose.elm.PagingState
import vivid.money.elmslie.samples.android.compose.elm.pagingStoreFactory

class PagingFragment : ElmComponentFragment<PagingEvent, PagingEffect, PagingState>() {

    override val initEvent = Ui.Init

    override fun createStore() = pagingStoreFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply { setContent { Screen() } }

    @Preview
    @Composable
    private fun Screen() {
        val state by state()
        val effect by effect()
        PagingScreen(
            state = state,
            effect = effect,
            onRefresh = { store.accept(Ui.PullToRefresh) },
            onReloadPage = { store.accept(Ui.ClickReloadPage) },
            onReloadScreen = { store.accept(Ui.ClickReloadScreen) },
            onCloseToEnd = { store.accept(Ui.CloseToEnd) }
        )
    }

    override fun handleEffect(effect: PagingEffect) = when (effect) {
        is PagingEffect.LoadError -> Unit // Not ui related effect will be handled here
        is PagingEffect.RefreshError -> Unit
    }
}