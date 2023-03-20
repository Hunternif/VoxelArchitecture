package hunternif.voxarch.editor.actions

import java.util.*

interface ReadOnlyHistory<T> {
    val pastItems: List<T>
    val futureItems: List<T>
    fun last(): T?
    fun hasPastItems(): Boolean
    fun hasFutureItems(): Boolean
}

class History<T> : ReadOnlyHistory<T> {
    override val pastItems = LinkedList<T>()
    override val futureItems = LinkedList<T>()

    /** Returns the last past item */
    override fun last(): T? = pastItems.last

    /**
     * Returns the last item and move current position back to it.
     * Calling [moveForward] after this will return the same item.
     * If there are no past items, returns null.
     */
    fun moveBack(): T? {
        if (!hasPastItems()) return null
        val item = pastItems.removeLast()
        futureItems.addFirst(item)
        return item
    }

    /**
     * Returns the next item and move current position forward to it.
     * Calling [moveBack] after this will return the same item.
     * If there are no future items, returns null.
     */
    fun moveForward(): T? {
        if (!hasFutureItems()) return null
        val item = futureItems.removeFirst()
        pastItems.addLast(item)
        return item
    }

    /**
     * Append a new item at the current position.
     * This will remove all future items.
     */
    fun append(item: T) {
        futureItems.clear()
        pastItems.add(item)
    }

    /** Whether there are any items from the start until current position. */
    override fun hasPastItems(): Boolean = pastItems.size > 1

    /** Whether there are any items after the current position. */
    override fun hasFutureItems(): Boolean = futureItems.isNotEmpty()

    fun clear() {
        pastItems.clear()
        futureItems.clear()
    }
}