package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.*
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.step
import hunternif.voxarch.vector.TransformationStack
import kotlin.math.sqrt

/**
 * Builds a "lancet" window on the given section of the wall.
 */
class ArchedWindowBuilder : AWallBuilder() {
    override fun build(node: Wall, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val midPoint = node.width / 2
        val height = node.height
        val depth = node.depth
        val local = world.toLocal(trans).asSymmetricX(midPoint)

        // for the top, fill an upside down circle.
        // use a larger radius for testing to get a nicer round arch.
        val r1 = midPoint
        val r2 = r1 + 0.4
        for (x in 0.0 .. midPoint step 1) {
            for (y in 0.0..height step 1) {
                if (y > (height - r1 + sqrt(r2 * r2 - (r1 - x) * (r1 - x))))
                    continue
                for (z in 0.0..depth step 1) {
                    local.clearBlock(x, y, z)
                }
            }
        }
    }
}