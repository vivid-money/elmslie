package vivid.money.elmslie.samples.android.loader.repository

import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

object ValueRepository {

    private val random = Random()

    @Suppress("MagicNumber")
    fun getValue() = Single.timer(random.nextLong() % 2000 + 1000, TimeUnit.MILLISECONDS)
        .map { random.nextInt() }
        .doOnSuccess { if (it % 3 == 1) error("Simulate unexpected error") }
}
