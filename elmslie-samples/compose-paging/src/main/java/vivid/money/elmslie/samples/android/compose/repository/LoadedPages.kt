package vivid.money.elmslie.samples.android.compose.repository

import vivid.money.elmslie.samples.android.compose.model.Page

/* Immutable class to prevent concurrency issues */
data class LoadedPages(
    val pages: List<Page> = emptyList()
) {
    constructor(page: Page) : this(listOf(page))

    operator fun plus(page: Page) = copy(pages = pages.asSequence().plus(page).distinct().toList())
}