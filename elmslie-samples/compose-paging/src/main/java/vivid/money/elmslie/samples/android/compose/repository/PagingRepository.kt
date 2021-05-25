package vivid.money.elmslie.samples.android.compose.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import vivid.money.elmslie.samples.android.compose.model.Page

class PagingRepository(
    private val api: FakeApi = FakeApi
) {

    private val cache = PagesCache

    /** Will never emit onError */
    fun observePages(): Observable<List<Page>> = cache.observe()

    fun loadPage(pageKey: String?): Completable = api.getPage(pageKey)
        .doOnSuccess(cache::addPage)
        .ignoreElement()

    fun refreshPages(): Completable = api.getPage(null)
        .doOnSuccess { cache.replaceAll(it) }
        .ignoreElement()
}