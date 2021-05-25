package vivid.money.elmslie.samples.android.compose.repository

import io.reactivex.rxjava3.core.Single
import vivid.money.elmslie.samples.android.compose.model.Item
import vivid.money.elmslie.samples.android.compose.model.Page
import java.util.*
import java.util.concurrent.TimeUnit

object FakeApi {

    private const val PAGE_SIZE = 20
    private val random = Random()

    fun getPage(key: String?): Single<Page> = Single.just(generatePage(key))
        .delay()
        .simulateError()

    private fun generatePage(key: String?): Page {
        val items = (0 until PAGE_SIZE).map { (key?.toIntOrNull() ?: 0) + it }.map(::Item)
        return Page(
            items = items,
            nextPageKey = (items.last().value + 1).toString()
        )
    }

    @Suppress("MagicNumber")
    private fun Single<Page>.delay() =
        delay(random.nextLong() % 2000 + 1000, TimeUnit.MILLISECONDS)

    private fun Single<Page>.simulateError() =
        doOnSuccess { if (random.nextBoolean()) error("api error!") }
}