package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.ANodeBuilder
import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.line
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PathSegment
import hunternif.voxarch.plan.getPerimeter
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack
import hunternif.voxarch.vector.Vec3

/**
 * Inscribe a pyramid into the room. Every section based on the walls.
 * This only builds the surfaces of the pyramid, making it hollow inside.
 */
class PyramidBuilder(
    private val material: String,
    private val upsideDown: Boolean = false
): ANodeBuilder() {
    override fun build(node: Node, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        val polygon = node.getPerimeter()
        if (upsideDown) {
            trans.push()
            trans.translate(0, node.height, 0)
            trans.mirrorY()
        }
        trans.push()
        trans.translate(polygon.origin)
        val apex = trans.transform(0.0, node.height, 0.0)
        polygon.segments.forEach {
            buildSegment(it, apex, trans, world, context)
        }
        trans.pop()
        if (upsideDown) {
            trans.pop()
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
        val p1 = trans.transform(segment.p1)
        val p2 = trans.transform(segment.p2)
        line(p1, p2, 0.5) {
            line(it, apex, 1.0, 0.1) { p ->
                val block = context.materials.get(material)
                world.setBlock(p, block)
            }
        }
    }
}