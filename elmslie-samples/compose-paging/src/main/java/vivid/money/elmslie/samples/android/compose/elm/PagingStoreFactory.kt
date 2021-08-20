package vivid.money.elmslie.samples.android.compose.elm

import vivid.money.elmslie.core.store.ElmStore
import vivid.money.elmslie.samples.android.compose.repository.FakeApi
import vivid.money.elmslie.samples.android.compose.repository.PagingRepository

fun pagingStoreFactory() = ElmStore(
    initialState = PagingState(),
    reducer = PagingReducer,
    actor = PagingActor(PagingRepository(FakeApi))
)
