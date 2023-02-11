package hunternif.voxarch.util

fun <T> List<T>.loopSequence() = sequence<T> {
    if (isEmpty()) throw NoSuchElementException()
    var i = 0
    while(true) {
        yield(get(i % count()))
        i++
    }
}

inline fun <T> MutableList<T?>.getOrInsert(i: Int, crossinline newValue: () -> T): T {
    return getOrNull(i) ?: run {
        val value = newValue()
        for (j in size..i) {
            add(null)
        }
        set(i, value)
        value
    }
}

inline fun <T> Collection<T>.ifNotEmpty(
    crossinline block: (Collection<T>) -> Unit,
) {
    if (isNotEmpty()) block(this)
}

inline fun String.ifNotEmpty(
    crossinline block: (String) -> Unit,
) {
    if (isNotEmpty()) block(this)
}

// Courtesy of https://stackoverflow.com/a/44332139/1093712
infix fun ClosedRange<Double>.step(step: Number): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step.toDouble() > 0.0) { "Step must be positive, was: $step." }
    if (start > endInclusive) return emptyList()
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step.toDouble()
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}