package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.asSymmetricX
import hunternif.voxarch.builder.toLocal
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack
import kotlin.math.sqrt

/**
 * Builds a "lancet" window on the given section of the wall.
 */
class ArchedWindowBuilder : Builder<Wall>() {
    override fun build(node: Wall, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val midPoint = (node.width / 2).toInt()
        val height = node.height.toInt()
        val depth = node.depth.toInt()
        val local = world.toLocal(trans).asSymmetricX(midPoint)
        // fill center line
        for (y in 0..height) {
            for (z in 0..depth) {
                local.clearBlock(midPoint, y, z)
            }
        }
        // for the top, fill an upside down circle
        val r = midPoint.toDouble()
        for (x in 0 until midPoint) {
            for (y in 0..height) {
                if (y > (height - r + sqrt(r*r - (r - x)*(r - x)).toInt()))
                    continue
                for (z in 0..depth) {
                    local.clearBlock(x, y, z)
                }
            }
        }
    }
}