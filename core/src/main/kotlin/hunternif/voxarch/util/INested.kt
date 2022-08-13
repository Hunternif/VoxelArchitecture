package hunternif.voxarch.util

import java.util.LinkedList

interface INested<T : INested<T>> {
    var parent: T?
    val children: Collection<T>

    fun iterateSubtree() = sequence<T> {
        val queue = LinkedList<T>()
        @Suppress("UNCHECKED_CAST")
        queue.add(this@INested as T)
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            yield(next)
            queue.addAll(next.children)
        }
    }
}