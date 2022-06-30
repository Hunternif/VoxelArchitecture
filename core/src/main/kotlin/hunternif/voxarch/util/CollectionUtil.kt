package hunternif.voxarch.util

fun <T> List<T>.loopSequence() = sequence<T> {
    if (isEmpty()) throw NoSuchElementException()
    var i = 0
    while(true) {
        yield(get(i % count()))
        i++
    }
}