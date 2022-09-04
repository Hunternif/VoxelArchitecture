package hunternif.voxarch.util

import java.util.LinkedList

interface INested<T : INested<T>> {
    var parent: T?
    /** Should only be modified via `addChild` and `removeChild`. */
    val children: MutableCollection<T>

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

    fun addChild(child: T) {
        child.parent?.removeChild(child)
        @Suppress("UNCHECKED_CAST")
        child.parent = this as T
        children.add(child)
    }

    fun removeChild(child: T): Boolean {
        if (children.remove(child)) {
            child.parent = null
            return true
        }
        return false
    }

    fun removeAllChildren() {
        val childrenCopy = children.toList()
        for (child in childrenCopy) {
            removeChild(child)
        }
    }
}