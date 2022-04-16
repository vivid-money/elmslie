package vivid.money.elmslie.samples.android.compose.elm

import vivid.money.elmslie.core.ElmStoreCompat
import vivid.money.elmslie.samples.android.compose.repository.FakeApi
import vivid.money.elmslie.samples.android.compose.repository.PagingRepository

fun pagingStoreFactory() = ElmStoreCompat(
    initialState = PagingState(),
    reducer = PagingReducer,
    actor = PagingActor(PagingRepository(FakeApi))
)
