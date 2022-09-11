package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.util.forEachSubtree

/**
 * An entity that unifies a single scene tree.
 * All items in the tree are linked to it.
 */
class SceneTree(
    /** Root of the tree structure, should be a dummy object. */
    val root: SceneObject
) : Iterable<SceneObject> {

    /** All nodes currently in the tree */
    val items: LinkedHashSet<SceneObject> = LinkedHashSet()
    override fun iterator(): Iterator<SceneObject> = items.iterator()

    init {
        root.tree = this
        add(root) // in case the root already has children
        items.remove(root)
    }

    /**
     * Subsets of objects within this tree.
     * When a subtree is detached, its children are removed from all subsets;
     * and when it's reattached, they are added back.
     */
    val subsets = mutableListOf<Subset<*>>()

    fun add(subtree: SceneObject) {
        items.addAll(subtree.iterateSubtree())
    }

    fun remove(subtree: SceneObject) {
        subtree.forEachSubtree { items.remove(it) }
    }
}

/** A subset of objects in the scene tree. */
open class Subset<T : SceneObject>(
    override val id: Int,
    private val name: String,
    private val items: LinkedHashSet<T> = LinkedHashSet(),
) : MutableSet<T> by items, WithID {
    override fun toString() = "Subset $name: [${size}]"
}