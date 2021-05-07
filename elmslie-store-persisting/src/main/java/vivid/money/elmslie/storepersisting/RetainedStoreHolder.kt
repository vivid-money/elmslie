package vivid.money.elmslie.storepersisting

import android.os.Bundle
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner

@MainThread
fun <R : ClearableStoreHolder<*, *, *>> createRetainedStoreHolderLazy(
    key: String,
    getViewModelStoreOwner: () -> ViewModelStoreOwner,
    getSavedStateRegistryOwner: () -> SavedStateRegistryOwner,
    getDefaultArgs: () -> Bundle,
    createStoreHolder: (SavedStateHandle) -> R,
): Lazy<R> = lazy(LazyThreadSafetyMode.NONE) {
    createRetainedStoreHolder(
        key = key,
        viewModelStoreOwner = getViewModelStoreOwner(),
        savedStateRegistryOwner = getSavedStateRegistryOwner(),
        defaultArgs = getDefaultArgs(),
        createStoreHolder = createStoreHolder
    )
}

private fun <R : ClearableStoreHolder<*, *, *>> createRetainedStoreHolder(
    key: String,
    viewModelStoreOwner: ViewModelStoreOwner,
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    createStoreHolder: (SavedStateHandle) -> R,
): R {
    val factory = RetainedViewModelFactory(savedStateRegistryOwner, defaultArgs, createStoreHolder)
    val provider = ViewModelProvider(viewModelStoreOwner, factory)

    @Suppress("UNCHECKED_CAST")
    return provider.get(key, RetainedViewModel::class.java).retainedStoreHolder as R
}

private class RetainedViewModel(
    savedStateHandle: SavedStateHandle,
    createStoreHolder: (SavedStateHandle) -> ClearableStoreHolder<*, *, *>,
) : ViewModel() {

    val retainedStoreHolder = createStoreHolder(savedStateHandle)

    override fun onCleared() {
        retainedStoreHolder.clear()
    }
}

private class RetainedViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle,
    private val createStoreHolder: (SavedStateHandle) -> ClearableStoreHolder<*, *, *>,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        @Suppress("UNCHECKED_CAST")
        return RetainedViewModel(handle, createStoreHolder) as T
    }
}
