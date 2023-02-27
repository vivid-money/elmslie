package vivid.money.elmslie.samples.android.compose.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import vivid.money.elmslie.compose.EffectWithKey
import vivid.money.elmslie.samples.android.compose.elm.PagingEffect
import vivid.money.elmslie.samples.android.compose.elm.PagingState

private const val SHIMMER_COUNT = 10

@Suppress("LongParameterList")
@Composable
fun PagingScreen(
    state: PagingState,
    effect: EffectWithKey<PagingEffect>?,
    onRefresh: () -> Unit,
    onReloadScreen: () -> Unit,
    onReloadPage: () -> Unit,
    onCloseToEnd: () -> Unit,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(scaffoldState = scaffoldState) { padding ->
        when {
            state.error != null && state.items == null -> ErrorState(onReloadScreen, padding)
            state.error == null && state.items == null -> Shimmers(padding)
            state.items != null -> List(state, onRefresh, onCloseToEnd, onReloadPage, padding)
        }
        effect?.takeIfInstanceOf<PagingEffect.RefreshError>()?.key?.let {
            Error(scaffoldState = scaffoldState, key = it)
        }
    }
}

@Composable
fun List(
    state: PagingState,
    onRefresh: () -> Unit,
    onCloseToEnd: () -> Unit,
    onReloadPage: () -> Unit,
    padding: PaddingValues
) {
    require(state.items != null)
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
        indicatorPadding = padding,
        onRefresh = onRefresh
    ) {
        val listState = rememberLazyListState()
        LazyColumn(state = listState) {
            items(state.items) { Item(text = "Value: ${it.value}") }
            if (state.error != null) {
                item { ReloadItemButton(onReloadPage = onReloadPage) }
            } else if (state.hasMorePages) {
                item { LoadingIndicator() }
            }
            item {
                LaunchedEffect(true) {
                    onCloseToEnd()
                }
            }
        }
    }
}

@Composable
fun Shimmers(
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.padding(padding)
    ) {
        items(SHIMMER_COUNT) { Item(text = "SHIMMER") }
    }
}

@Composable
fun ErrorState(
    onReloadScreen: () -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error")
        Button(onClick = onReloadScreen) {
            Text(text = "Reload")
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ReloadItemButton(onReloadPage: () -> Unit) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onReloadPage) {
            Text(text = "Reload")
        }
    }
}

@Composable
fun Item(
    text: String
) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

@Composable
fun Error(
    scaffoldState: ScaffoldState,
    key: Any
) {
    LaunchedEffect(key) {
        scaffoldState.snackbarHostState.showSnackbar("Error!")
    }
}
