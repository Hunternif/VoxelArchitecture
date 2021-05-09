package hunternif.voxarch.sandbox.castle

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.vector.Vec3
import kotlin.math.ceil
import kotlin.math.sqrt

/**
 * Inscribe a pyramid into the room.
 */
class PyramidBuilder(
    private val material: String,
    private val sideCount: Int = 4
): Builder<Room>() {
    override fun build(node: Room, world: IBlockStorage, context: BuildContext) {
        val a = node.size.x/2
        val b = node.size.z/2
        val tip = Vec3(0.0, node.height, 0.0)

        if (sideCount == 4) {
            buildSection(Vec3(a, 0.0, b), Vec3(a, 0.0, -b), tip, world, context)
            buildSection(Vec3(a, 0.0, -b), Vec3(-a, 0.0, -b), tip, world, context)
            buildSection(Vec3(-a, 0.0, -b), Vec3(-a, 0.0, b), tip, world, context)
            buildSection(Vec3(-a, 0.0, b), Vec3(a, 0.0, b), tip, world, context)
        } else {
            val angleStep = 360.0 / sideCount.toDouble()
            // Going counterclockwise:
            var angle = -angleStep / 2
            while (angle < 360 - angleStep / 2) {
                buildSection(
                    Vec3(
                        a * MathUtil.cosDeg(angle),
                        0.0,
                        -b * MathUtil.sinDeg(angle)
                    ),
                    Vec3(
                        a * MathUtil.cosDeg(angle + angleStep),
                        0.0,
                        -b * MathUtil.sinDeg(angle + angleStep)
                    ),
                    tip,
                    world,
                    context
                )
                angle += angleStep
            }
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

    private fun fillTriangle(
        base: Double,
        slopeLength: Double,
        world: IBlockStorage,
        context: BuildContext
    ) {
        val lengthInt = ceil(slopeLength).toInt()
        val baseInt = ceil(base).toInt()
        for (dz in 0 .. lengthInt) {
            for (x in 0 .. baseInt) {
                if (x <= dz * baseInt / lengthInt) {
                    val z = dz - lengthInt
                    var block = context.materials.get(material)
                    world.setBlock(x, 0, z, block)

                    block = context.materials.get(material)
                    world.setBlock(-x, 0, z, block)
                }
            }
        }
    }
}