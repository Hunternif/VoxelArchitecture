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

    /** Attach a child to this object */
    fun addChild(child: T) {
        child.parent?.removeChild(child)
        child.parent = this as T
        children.add(child)
    }

    /** Attach multiple children to this object */
    fun attachAll(items: Collection<T>) {
        items.forEach { addChild(it) }
    }

    /** Remove a child from this object, if it exists */
    fun removeChild(child: T): Boolean {
        if (children.remove(child)) {
            child.parent = null
            return true
        }
        return false
    }

    /** Remove this object from its parent */
    fun remove(): Boolean = parent?.removeChild(this as T) ?: false

    fun removeAllChildren() {
        val childrenCopy = children.toList()
        for (child in childrenCopy) {
            removeChild(child)
        }
    }
}