package hunternif.voxarch.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.storage.ClippedBlockStorage
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.LinearTransformation
import java.util.*
import kotlin.collections.LinkedHashSet

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
        buildQueue.add(newTransform(rootNode, world))

        // Deferred queue for things to be built after all other nodes.
        // Windows go here by default.
        val deferredQueue = LinkedList<Entry>()
        var buildingDeferred = false

        // Use Depth-first search, so that inner rooms and walls are built
        // before outer siblings.
        while (buildQueue.isNotEmpty()) {
            val entry = buildQueue.removeFirst()
            val node = entry.node
            if (node in visited) continue
            visited.add(node)
            listeners.forEach { it.onBeginBuild(node) }

            val builder = context.builders.get(node)
            if (builder != null && !node.transparent) {
                builder.build(node, entry.trans, entry.world, context)
            }

            val sortedChildren = LinkedHashSet<Node>().apply {
                addAll(node.children.filterIsInstance<Floor>())
                addAll(node.children.filterIsInstance<Wall>())
                // all children except windows:
                addAll(node.children.filterNot { it.isWindow() })
            }
            // only windows are left, defer them:
            if (buildingDeferred) {
                sortedChildren.addAll(node.children)
            } else {
                val deferred = LinkedHashSet<Node>().apply {
                    node.children.forEach { if (it !in sortedChildren) add(it) }
                }
                deferredQueue.addAll(0, deferred.map {
                    continueTransform(it, entry.trans.clone(), entry.world)
                })
            }

            listeners.forEach { it.onPrepareChildren(node, sortedChildren) }

            buildQueue.addAll(0, sortedChildren.map {
                continueTransform(it, entry.trans.clone(), entry.world)
            })

            if (buildQueue.isEmpty() && !buildingDeferred) {
                buildingDeferred = true
                buildQueue.addAll(deferredQueue)
            }
        }
    }

    private fun Node.isWindow() = this is Window || this is Gate || this is Hatch

    private data class Entry(
        val node: Node,
        val trans: LinearTransformation,
        val world: IBlockStorage,
    )

    private fun newTransform(node: Node, world: IBlockStorage) =
        continueTransform(node, LinearTransformation(), world)

    private fun continueTransform(
        node: Node,
        trans: LinearTransformation,
        world: IBlockStorage,
    ): Entry {
        val newTrans = trans.apply {
            translate(node.origin)
            rotateY(node.rotationY)
        }
        val newWorld = when (node.clipMask) {
            ClipMask.OFF -> world
            else -> ClippedBlockStorage(world, node)
        }
        return Entry(node, newTrans, newWorld)
    }
}