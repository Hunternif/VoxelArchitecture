package hunternif.voxarch.builder

import hunternif.voxarch.plan.Structure
import hunternif.voxarch.storage.IBlockStorage

/**
 * Use this builder to start building in an open world.
 * Don't register it in [BuildContext]
 */
class MainBuilder : Builder<Structure>() {
    /**
     * Moves starting point to `node`'s origin and then starts building.
     */
    override fun build(node: Structure, world: IBlockStorage, context: BuildContext) {
        world.transformer().apply {
            pushTransformation()
            translate(node.origin)
            rotateY(node.rotationY)
            super.build(node, this, context)
            popTransformation()
        }
    }
}