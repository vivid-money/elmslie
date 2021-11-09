package vivid.money.elmslie.compose.util

import androidx.compose.runtime.*
import vivid.money.elmslie.core.disposable.Disposable

/**
 * Subscribes to the callback and represents it's values via [State]
 */
@Composable
fun <T> (((T) -> Unit) -> Disposable).subscribeAsState(
    initial: T
): State<T> = subscribeAsState({ it }, initial = initial)

/**
 * Subscribes to the callback and represents it's values via [State] with applied [transformation]
 */
@Composable
fun <T, V> (((T) -> Unit) -> Disposable).subscribeAsState(
    transformation: (T) -> V,
    initial: V
): State<V> {
    val state = remember { mutableStateOf(initial) }
    DisposableEffect(this) {
        val disposable = this@subscribeAsState { state.value = transformation(it) }
        onDispose { disposable.dispose() }
    }
    return state
}
