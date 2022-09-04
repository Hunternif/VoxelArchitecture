package hunternif.voxarch.util

import java.util.LinkedList

@Suppress("UNCHECKED_CAST")
interface INested<T : INested<T>> {
    var parent: T?
    /** Should only be modified via `addChild` and `removeChild`. */
    val children: MutableCollection<T>

    fun iterateSubtree() = sequence<T> {
        val queue = LinkedList<T>()
        queue.add(this@INested as T)
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            yield(next)
            queue.addAll(next.children)
        }
    }

    fun addChild(child: T) {
        child.parent?.removeChild(child)
        child.parent = this as T
        child.onAdded()
        children.add(child)
    }

    fun onAdded() {}

    fun removeChild(child: T): Boolean {
        if (children.remove(child)) {
            child.onRemoved()
            child.parent = null
            return true
        }
        return false
    }

    fun onRemoved() {}

    fun removeAllChildren() {
        val childrenCopy = children.toList()
        for (child in childrenCopy) {
            removeChild(child)
        }
    }
}