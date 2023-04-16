package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.ILinearTransformation

/**
 * Builds any [Node] into "physical" blocks inside a world,
 * recursively calling other Builders for nested Nodes.
 *
 * Custom Builder subclasses can handle Node subclasses ("Room", "Wall" etc)
 * and types ("corridor", "dungeon" etc).
 */
open class Builder<in T : Node>(
    val nodeClass: Class<in T>,
) {
    companion object {
        inline operator fun <reified T : Node> invoke(): Builder<T> =
            Builder(T::class.java)
    }

    /**
     * Override this, placing blocks in the world to represent this Node.
     * No need to build children, they are built automatically after this.
     *
     * @param node the node being built into blocks.
     * @param trans transforms local coordinates to global world coordinates.
     *      Must be rotated with respect to `node.rotationY`
     *      and positioned so that (0, 0, 0) is at the Node's origin.
     * @param world uses global coordinates.
     * @param context contains materials and custom Builders.
     */
    internal open fun build(
        node: T,
        trans: ILinearTransformation,
        world: IBlockStorage,
        context: BuildContext,
    ) {}

    override fun toString(): String = this::class.java.simpleName
}