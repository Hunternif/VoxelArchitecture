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