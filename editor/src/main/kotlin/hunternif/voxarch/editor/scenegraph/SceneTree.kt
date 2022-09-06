package hunternif.voxarch.editor.scenegraph

/**
 * An entity that unifies a single scene tree.
 * All items in the tree are linked to it.
 */
class SceneTree : Iterable<SceneObject> {
    //TODO use this to replace rootNode, selectedNodes, hiddenNodes as SUBSETS.

    // Subsets of this tree.
    // When a node is detached, it's removed from all subsets.
    // Subsets can be iterated as a tree element, to recursively apply a feature
    // to its children.
    // Using a single TreeNode class to represent the hierarchy might help,
    // e.g. when I'm combining Nodes and Voxel group inside a selection subset.
    //private val subsets: List<TreeSubset<T>>

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
    val subsets = mutableListOf<Subset>()

    /** A subset of objects in the scene tree. */
    class Subset(
        private val name: String,
        private val items: LinkedHashSet<SceneObject> = LinkedHashSet(),
    ) : MutableSet<SceneObject> by items {
        override fun toString() = "Subset $name: [${size}]"
    }

    fun onAttach(subtree: SceneObject) {
        items.addAll(subtree.iterateSubtree())
    }

    fun onReattach(detached: DetachedObject) {
        detached.memberships.forEach { (subset, objectSet) ->
            subset.addAll(objectSet)
        }
    }

    fun onDetach(detached: DetachedObject) {
        detached.obj.iterateSubtree().forEach { child ->
            items.remove(child)
            subsets.forEach { it.remove(child) }
        }
    }
}