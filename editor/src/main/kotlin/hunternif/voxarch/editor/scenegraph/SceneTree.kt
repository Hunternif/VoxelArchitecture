package hunternif.voxarch.editor.scenegraph

import hunternif.voxarch.plan.Node

/**
 * Encapsulates access and modification of a tree of SceneNodes.
 * Ensures the dual hierarchy of SceneNodes and Nodes is maintained.
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

    /** All nodes currently in the tree */
    val nodes: LinkedHashSet<SceneObject> = LinkedHashSet()

    override fun iterator(): Iterator<SceneObject> = nodes.iterator()

    fun attach(parent: SceneObject, subtree: SceneObject) {
        subtree.parent = parent
        if (subtree !in parent.children) {
            parent.addChild(subtree)
            subtree.iterateSubtree().forEach { nodes.add(it) }
        }
    }

    fun attach(detached: DetachedObject) {
        detached.parent?.let { attach(it, detached.node) }
    }

    fun attach(parent: SceneObject, subtree: Node) {
        attach(parent, wrap(subtree))
    }

    fun attachAll(parent: SceneObject, items: Collection<SceneObject>) {
        items.forEach { attach(parent, it) }
    }

    fun attachAll(detachedItems: Collection<DetachedObject>) {
        detachedItems.forEach { attach(it) }
    }

    fun detach(subtree: SceneObject) {
        subtree.parent?.removeChild(subtree)
        subtree.iterateSubtree().forEach { nodes.remove(it) }
    }

    fun detach(detached: DetachedObject) {
        detach(detached.node)
    }

    fun detachAll(items: Collection<SceneObject>) {
        items.forEach { detach(it) }
    }
}

/** Contains information about a node after it's removed from the tree,
 * so that it can be attached again. */
data class DetachedObject(
    val parent: SceneObject?,
    val node: SceneObject,
)

fun SceneObject.detached() = DetachedObject(parent, this)

/** Create a SceneNode tree matching the hierarchy of the node tree. */
fun wrap(tree: Node): SceneNode {
    val wrapperMap = mutableMapOf<Node, SceneNode>()
    tree.iterateSubtree().forEach { node ->
        val newWrapper = SceneNode(node)
        wrapperMap[node] = newWrapper
        node.parent?.let { wrapperMap[it] }?.children?.add(newWrapper)
    }
    return wrapperMap[tree]!!
}