package hunternif.voxarch.util

import kotlin.random.Random

/**
 * Gets the next random *even* Int up to [until] (exclusive).
 */
fun Random.nextEvenInt(until: Int): Int {
    return nextEvenInt(0, until + 1)
}

/**
 * Gets the next random *even* Int from the given [range].
 */
fun Random.nextEvenInt(range: IntRange): Int {
    return nextEvenInt(range.first, range.last + 1)
}

/**
 * Gets the next random *even* Int between [from] (inclusive)
 * and [until] (exclusive).
 */
fun Random.nextEvenInt(from: Int, until: Int): Int {
    require(until > from) { "until < from" }
    val startPadding = if (from % 2 == 0) 0 else 1
    val start = from / 2 + startPadding
    val end = until / 2
    // +1 because [until] it's exclusive
    return this.nextInt(start, end + 1) * 2
}


/**
 * Selects one from the given items with equal probability.
 */
fun <T> Random.next(vararg items: T): T = items[nextInt(items.size)]


interface IRandomOption { val probability: Double }
class RandomOption<T>(
    override val probability: Double,
    val value: T
) : IRandomOption

/**
 * Selects one from the given options with _unequal_ probability.
 * Probabilities should be >= 0, but need not add up to 1.
 * At least one option should have probability > 0.
 */
fun <O : IRandomOption> Random.nextWeighted(vararg options: O): O =
    nextWeighted(options.toList())

/**
 * Selects one from the given options with _unequal_ probability.
 * Probabilities should be >= 0, but need not add up to 1.
 * At least one option should have probability > 0.
 */
fun <O : IRandomOption> Random.nextWeighted(options: Collection<O>): O {
    require(options.isNotEmpty()) { "options are empty" }
    val sumTotal = options.sumOf { it.probability }
    var toss = this.nextDouble() * sumTotal
    for (opt in options) {
        if (opt.probability <= 0) continue
        toss -= opt.probability
        if (toss <= 0) return opt
    }
    // This should never occur
    return options.first()
}


/**
 * Gets the next random Double between [from] (incl.) and [to] (excl.).
 * If [to] <= [from], returns [to].
 */
fun Random.nextDoubleOrMax(from: Double, to: Double): Double {
    if (to <= from) return to
    return this.nextDouble(from, to)
}