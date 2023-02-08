package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

/**
 * Builds any [Node] into "physical" blocks inside a world,
 * recursively calling other Builders for nested Nodes.
 *
 * Custom Builder subclasses can handle Node subclasses ("Room", "Wall" etc)
 * and types ("corridor", "dungeon" etc).
 */
open class Builder<in T : Node> {
    /**
     * When overriding, don't forget to call [buildChildren]
     * and set `node.isBuilt = true` at some point.
     *
     * @param node the node being built into blocks.
     * @param trans transforms local coordinates to global world coordinates.
     *      Must be rotated with respect to `node.rotationY`
     *      and positioned so that (0, 0, 0) is at the Node's origin.
     * @param world uses global coordinates.
     * @param context contains materials and custom Builders.
     */
    open fun build(node: T, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        buildChildren(node, trans, world, context)
        node.isBuilt = true
    }

    internal fun buildChildren(node: T, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        node.children.filter { !it.isBuilt }.forEach { buildChild(it, trans, world, context) }
    }

    internal fun buildChild(child: Node, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val builder = context.builders.get(child)
        if (builder != null) {
            trans.apply {
                push()
                translate(child.position)
                rotateY(child.rotationY)
                translate(child.origin)
                builder.build(child, this, world, context)
                pop()
            }
        }
    }
}