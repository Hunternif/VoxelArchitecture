package hunternif.voxarch.editor.scenegraph

/**
 * An entity that unifies a single scene tree.
 * All items in the tree are linked to it.
 */
class SceneTree : Iterable<SceneObject> {
    /** Dummy object, root of the tree structure. */
    val root: SceneObject = SceneObject().apply { tree = this@SceneTree }

    /** All nodes currently in the tree */
    val items: LinkedHashSet<SceneObject> = LinkedHashSet()
    override fun iterator(): Iterator<SceneObject> = items.iterator()

    /**
     * Subsets of objects within this tree.
     * When a subtree is detached, its children are removed from all subsets;
     * and when it's reattached, they are added back.
     */
    val subsets = mutableListOf<Subset<*>>()

    /** A subset of objects in the scene tree. */
    open class Subset<T : SceneObject>(
        override val id: Int,
        private val name: String,
        private val items: LinkedHashSet<T> = LinkedHashSet(),
    ) : MutableSet<T> by items, WithID {
        override fun toString() = "Subset $name: [${size}]"
    }

    fun onAttach(subtree: SceneObject) {
        items.addAll(subtree.iterateSubtree())
    }

    fun onReattach(detached: DetachedObject) {
        detached.memberships.forEach { it.restore() }
    }

    fun onDetach(detached: DetachedObject) {
        detached.memberships.forEach { it.clear() }
        detached.obj.iterateSubtree().forEach { items.remove(it) }
    }
}