package hunternif.voxarch.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack

/**
 * Use this builder to start building in an open world.
 * Don't register it in [BuildContext]
 */
class MainBuilder : Builder<Node>() {
    /**
     * Moves starting point to `node`'s origin and then starts building.
     */
    fun build(node: Node, world: IBlockStorage, context: BuildContext) {
        val trans = TransformationStack()
        trans.apply {
            push()
            translate(node.position)
            rotateY(node.rotationY)
            translate(node.origin)
            super.build(node, this, world, context)
            pop()
        }
    }
}