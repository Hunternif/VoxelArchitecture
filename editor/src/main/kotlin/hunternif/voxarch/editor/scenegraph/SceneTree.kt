package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.util.forEachSubtree

/**
 * An entity that unifies a single scene tree.
 * All items in the tree are linked to it.
 *
 * All adding and removing of objects in the scene should happen through here.
 */
class SceneTree(
    /** Root of the tree structure, should be a dummy object. */
    val root: SceneObject
) : Iterable<SceneObject> {

    /** All nodes currently in the tree */
    val items: LinkedHashSet<SceneObject> = LinkedHashSet()
    override fun iterator(): Iterator<SceneObject> = items.iterator()

    /**
     * Subsets of objects within this tree.
     * When a subtree is detached, its children are removed from all subsets;
     * and when it's reattached, they are added back.
     */
    val subsets = mutableListOf<Subset<*>>()

    private val listeners = mutableListOf<ISceneListener>()

    init {
        root.tree = this
        add(root) // in case the root already has children
        items.remove(root)
    }

    /**
     * All objects added to the scene should go through this method.
     */
    fun add(subtree: SceneObject) {
        items.addAll(subtree.iterateSubtree())
        listeners.forEach { it.onSceneChange() }
    }

    /**
     * All objects removed from the scene should go through this method.
     */
    fun remove(subtree: SceneObject) {
        subtree.forEachSubtree { obj ->
            items.remove(obj)
            subsets.forEach { it.remove(obj) }
        }
        listeners.forEach { it.onSceneChange() }
    }

    fun addListener(listener: ISceneListener) {
        listeners.add(listener)
    }
}

/** A subset of objects in the scene tree. */
open class Subset<T : SceneObject>(
    override val id: Int,
    val name: String,
    internal val items: LinkedHashSet<T> = LinkedHashSet(),
) : MutableSet<T> by items, WithID {
    override fun toString() = "Subset $name: [${size}]"
}

interface ISceneListener {
    fun onSceneChange()
}