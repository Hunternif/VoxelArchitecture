package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage

/**
 * Builds any [Node] into "physical" blocks inside a world,
 * recursively calling other Builders for nested Nodes.
 *
 * Custom Builder subclasses can handle Node subclasses ("Room", "Wall" etc)
 * and types ("corridor", "dungeon" etc).
 */
open class Builder<in T : Node> {
    /**
     * Assuming that the [world] has been rotated with respect to [Node.rotationY]
     * and positioned so that (0, 0, 0) is at the Node's origin.
     *
     * When overriding, don't forget to call [buildChildren]
     * and set `node.isBuilt = true` at some point.
     *
     * @param context set up materials and custom Builders.
     */
    open fun build(node: T, world: IBlockStorage, context: BuildContext) {
        buildChildren(node, world, context)
        node.isBuilt = true
    }

    internal fun buildChildren(node: T, world: IBlockStorage, context: BuildContext) {
        node.children.filter { !it.isBuilt }.forEach { buildChild(it, world, context) }
    }

    internal fun buildChild(child: Node, world: IBlockStorage, context: BuildContext) {
        val builder = context.builders.get(child)
        if (builder != null) {
            world.transformer().apply {
                pushTransformation()
                translate(child.origin)
                rotateY(child.rotationY)
                builder.build(child, this, context)
                popTransformation()
            }
        }
    }
}