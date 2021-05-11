package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.transformer
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.util.PositionTransformer
import hunternif.voxarch.vector.Vec3
import kotlin.math.sqrt

/**
 * Inscribe a pyramid into the room. Every section based on the walls.
 */
class PyramidBuilder(
    private val material: String
): Builder<Room>() {
    override fun build(node: Room, world: IBlockStorage, context: BuildContext) {
        val tip = Vec3(0.0, node.height, 0.0)
        node.walls.forEach {
            val p1 = it.origin
            val p2 = Vec3(it.end.x, 0.0, it.end.z)
            buildSection(p1, p2, tip, world, context)
        }
        super.build(node, world, context)
    }

    private fun buildSection(
        p1: Vec3,
        p2: Vec3,
        tip: Vec3,
        world: IBlockStorage,
        context: BuildContext
    ) {
        val base = p2.distanceTo(p1) / 2
        val sectionAngle = MathUtil.atan2Deg(-p2.z + p1.z, p2.x - p1.x)
        val altitude = p1.add(p2).length() / 2
        val slopeAngle = MathUtil.atan2Deg(tip.y, altitude)
        val slopeLength = sqrt(tip.y * tip.y + altitude * altitude)

        val transformer = world.transformer()
        transformer.pushTransformation()
        transformer.translate(p1)
        transformer.rotateY(sectionAngle)
        transformer.translate(base, 0.0, 0.0)
        transformer.rotateX(slopeAngle)
        fillTriangle(base, slopeLength, transformer, context)
        transformer.popTransformation()
    }

    /**
     * ```
     *          Z ^
     *            | (outside)
     *  X <-------+--------
     *       \base|base/
     *        \   |   /
     *         \  |  /
     *          \ | /
     *           \|/   | dz
     *          (tip)
     * ```
     */
    private fun fillTriangle(
        base: Double,
        slopeLength: Double,
        transformer: PositionTransformer,
        context: BuildContext
    ) {
        var dz = 0.0
        while (dz <= slopeLength) {
            var x = 0.0
            while (x <= base) {

                if (x <= dz * base / slopeLength) {
                    val z = dz - slopeLength
                    var block = context.materials.get(material)
                    transformer.setBlock(x, 0.0, z, block)

                    block = context.materials.get(material)
                    transformer.setBlock(-x, 0.0, z, block)
                }

                //TODO: fix holes in pyramid
                x += 1.0
            }
            dz += 1.0
        }
    }
}