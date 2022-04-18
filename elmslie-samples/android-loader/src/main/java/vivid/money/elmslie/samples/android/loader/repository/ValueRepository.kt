package vivid.money.elmslie.samples.android.loader.repository

import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

object ValueRepository {

    private val predefinedValues = LinkedList<Pair<Boolean, Int>>()
    private val random = Random()

    @Suppress("MagicNumber")
    fun getValue() = Single
        .timer(random.nextLong() % 2000 + 1000, TimeUnit.MILLISECONDS)
        .map { calculateValue() }

    private fun calculateValue(): Int {
        if (predefinedValues.isEmpty()) return random.nextInt()
        val (isNumber, number) = predefinedValues.pop()
        if (isNumber) return number
        error("Simulated error")
    }

    fun predefineError() = predefinedValues.push(false to 0)

    fun predefineNumber(number: Int) = predefinedValues.push(true to number)
}
