package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.line
import hunternif.voxarch.plan.PathSegment
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.util.intRoundDown
import hunternif.voxarch.vector.TransformationStack
import hunternif.voxarch.vector.Vec3

/**
 * Inscribe a pyramid into the room. Every section based on the walls.
 * This only builds the surfaces of the pyramid, making it hollow inside.
 */
class PyramidBuilder(
    private val material: String,
    private val upsideDown: Boolean = false
): Builder<PolygonRoom>() {
    override fun build(node: PolygonRoom, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val apex = Vec3(0.0, node.height, 0.0)
        node.polygon.segments.forEach {
            buildSegment(it, apex, trans, world, context)
        }
        super.build(node, trans, world, context)
    }

    /**
     * Builds a triangle section of the pyramid.
     * Thanks to theworldfoundry for the line idea
     */
    private fun buildSegment(
        segment: PathSegment,
        apex: Vec3,
        trans: TransformationStack,
        world: IBlockStorage,
        context: BuildContext
    ) {
        line(segment.p1, segment.p2, 0.5) {
            line(it, apex, 1.0, 0.1) { p ->
                val block = context.materials.get(material)
                val y = if (upsideDown) apex.y - p.y else p.y
                val pos = trans.transform(p.x, y, p.z).intRoundDown()
                world.setBlock(pos, block)
            }
        }
    }
}