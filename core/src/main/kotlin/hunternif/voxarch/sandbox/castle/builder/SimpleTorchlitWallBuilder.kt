package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.MAT_TORCH
import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.builder.toLocal
import hunternif.voxarch.plan.Node
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.Direction
import hunternif.voxarch.vector.ILinearTransformation

class SimpleTorchlitWallBuilder(
    wallMaterial: String,
    private val torchWallSpacing: Int = 4,
    private val torchHeight: Int = 3
) : SimpleWallBuilder(wallMaterial) {

    override fun build(node: Node, trans: ILinearTransformation, world: IBlockStorage, context: BuildContext) {
        super.build(node, trans, world, context)
        val localWorld = world.toLocal(trans)

        //TODO: some torches fall down. Consider spawning them as props.

        // Starting with a half-step from the edge, and not including the edge
        // itself because it will probably be covered by another wall:
        var x = torchWallSpacing / 2
        while (x < node.width) {
            val block = context.materials.get(MAT_TORCH).orient(Direction.NORTH)
            localWorld.setBlock(x, torchHeight, -1, block)
            x += torchWallSpacing
        }
    }
}