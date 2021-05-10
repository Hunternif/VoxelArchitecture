package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.SimpleWallBuilder
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.sandbox.castle.MAT_TORCH
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.Direction

class SimpleTorchlitWallBuilder(
    wallMaterial: String,
    private val torchWallSpacing: Int = 4,
    private val torchHeight: Int = 3
) : SimpleWallBuilder(wallMaterial) {

    override fun build(node: Wall, world: IBlockStorage, context: BuildContext) {
        if (node.transparent) return
        super.build(node, world, context)

        //TODO: some torches fall down. Consider spawning them as props.

        // Starting with a half-step from the edge, and not including the edge
        // itself because it will probably be covered by another wall:
        var x = torchWallSpacing / 2
        while (x < node.length) {
            val block = context.materials.get(MAT_TORCH)
            block.orientation = Direction.NORTH
            world.setBlock(x, torchHeight, -1, block)
            x += torchWallSpacing
        }
    }
}