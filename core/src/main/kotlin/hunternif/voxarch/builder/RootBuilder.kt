package hunternif.voxarch.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.LinearTransformation
import java.util.*

/**
 * Use this builder to start building in an open world.
 * Don't register it in [BuildContext]
 */
class RootBuilder : ANodeBuilder() {

    private val listeners = mutableListOf<IBuildListener>()

    fun addListener(listener: IBuildListener) {
        listeners.add(listener)
    }

    /**
     * Moves starting point to [rootNode]'s origin and then starts building.
     */
    fun build(rootNode: Node, world: IBlockStorage, context: BuildContext) {
        val buildQueue = LinkedList<Entry>()
        val visited = mutableSetOf<Node>()
        buildQueue.add(newTransform(rootNode))

        // Use Depth-first search, so that inner rooms and walls are built
        // before holes (e.g. gates, windows)
        while (buildQueue.isNotEmpty()) {
            val entry = buildQueue.removeFirst()
            val node = entry.node
            if (node in visited) continue
            visited.add(node)
            listeners.forEach { it.onBeginBuild(node) }

            val builder = context.builders.get(node)
            if (builder != null && !node.transparent) {
                builder.build(node, entry.trans, world, context)
            }

            val sortedChildren = LinkedHashSet<Node>().apply {
                addAll(node.children.filterIsInstance<Floor>())
                addAll(node.children.filterIsInstance<Wall>())
                // all children except windows:
                addAll(node.children.filterNot { it.isWindow() })
                // only windows are left:
                addAll(node.children)
            }.toList()
            listeners.forEach { it.onPrepareChildren(node, sortedChildren) }

            buildQueue.addAll(0, sortedChildren.map {
                continueTransform(it, entry.trans.clone())
            })
        }
    }

    private fun Node.isWindow() = this is Window || this is Gate || this is Hatch

    private data class Entry(
        val node: Node,
        val trans: LinearTransformation,
    )

    private fun newTransform(node: Node) =
        continueTransform(node, LinearTransformation())

    private fun continueTransform(
        node: Node,
        trans: LinearTransformation,
    ): Entry {
        val newTrans = trans.apply {
            translate(node.origin)
            rotateY(node.rotationY)
        }
        return Entry(node, newTrans)
    }
}