package vivid.money.elmslie.samples.android.compose.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import vivid.money.elmslie.samples.android.compose.model.Page

object PagesCache {

    private val cache = BehaviorSubject.create<LoadedPages>()

    fun addPage(page: Page) = cache.onNext((cache.value ?: LoadedPages()) + page)
    fun replaceAll(page: Page) = cache.onNext(LoadedPages(page))
    fun observe(): Observable<List<Page>> = cache.map { it.pages }
}