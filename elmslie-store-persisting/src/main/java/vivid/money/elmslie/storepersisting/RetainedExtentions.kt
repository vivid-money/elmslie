package vivid.money.elmslie.storepersisting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

internal inline fun <reified R : ClearableStoreHolder<*, *, *>> ComponentActivity.retain(
    key: String = R::class.java.name,
    noinline getDefaultArgs: () -> Bundle = { intent.extras ?: Bundle() },
    noinline createRetainedStoreHolder: (SavedStateHandle) -> R,
): Lazy<R> {
    val getActivity: () -> ComponentActivity = { this }
    return createRetainedStoreHolderLazy(
        key = key,
        getViewModelStoreOwner = getActivity,
        getSavedStateRegistryOwner = getActivity,
        getDefaultArgs = getDefaultArgs,
        createStoreHolder = createRetainedStoreHolder
    )
}

internal inline fun <reified R : ClearableStoreHolder<*, *, *>> Fragment.retain(
    key: String = R::class.java.name,
    noinline getDefaultArgs: () -> Bundle = { arguments ?: Bundle() },
    noinline createRetainedStoreHolder: (SavedStateHandle) -> R,
): Lazy<R> {
    val getFragment: () -> Fragment = { this }
    return createRetainedStoreHolderLazy(
        key = key,
        getViewModelStoreOwner = getFragment,
        getSavedStateRegistryOwner = getFragment,
        getDefaultArgs = getDefaultArgs,
        createStoreHolder = createRetainedStoreHolder
    )
}

internal inline fun <reified R : ClearableStoreHolder<*, *, *>> Fragment.retainInActivity(
    key: String = R::class.java.name,
    noinline getDefaultArgs: () -> Bundle = { activity?.intent?.extras ?: Bundle() },
    noinline createRetainedStoreHolder: (SavedStateHandle) -> R,
): Lazy<R> = createRetainedStoreHolderLazy(
    key = key,
    getViewModelStoreOwner = ::requireActivity,
    getSavedStateRegistryOwner = ::requireActivity,
    getDefaultArgs = getDefaultArgs,
    createStoreHolder = createRetainedStoreHolder
)

internal inline fun <reified R : ClearableStoreHolder<*, *, *>> Fragment.retainInParent(
    key: String = R::class.java.name,
    noinline getDefaultArgs: () -> Bundle = ::parentDefaultArgs,
    noinline createRetainedStoreHolder: (SavedStateHandle) -> R,
): Lazy<R> = createRetainedStoreHolderLazy(
    key = key,
    getViewModelStoreOwner = ::parentViewModelStoreOwner,
    getSavedStateRegistryOwner = ::parentSavedStateRegistryOwner,
    getDefaultArgs = getDefaultArgs,
    createStoreHolder = createRetainedStoreHolder
)

@PublishedApi
internal val Fragment.parentDefaultArgs: Bundle
    get() = parentFragment?.arguments ?: activity?.intent?.extras ?: Bundle()

@PublishedApi
internal val Fragment.parentViewModelStoreOwner: ViewModelStoreOwner
    get() = parentFragment ?: requireActivity()

@PublishedApi
internal val Fragment.parentSavedStateRegistryOwner: SavedStateRegistryOwner
    get() = parentFragment ?: requireActivity()