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

/** Returns true if both collections contain the same elements */
fun isCollectionEqual(a: Collection<*>, b: Collection<*>): Boolean {
    if (a.size != b.size) return false
    val ia = a.iterator()
    val ib = b.iterator()
    while (ia.hasNext() && ib.hasNext()) {
        if (ia.next() != ib.next()) return false
    }
    return true
}

/** Iterates in reverse order */
inline fun <T> List<T>.forEachReversed(action: (T) -> Unit) {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        action(element)
    }
}