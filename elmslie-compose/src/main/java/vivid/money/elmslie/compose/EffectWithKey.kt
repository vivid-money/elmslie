package vivid.money.elmslie.compose

import androidx.compose.runtime.Stable

// Not a data class intentionally
@Stable
class EffectWithKey<T>(val value: T) {
    val key = this

    @Suppress("UNCHECKED_CAST")
    inline fun <reified R : T> takeIfInstanceOf() = takeIf { value is R } as EffectWithKey<R>?
}