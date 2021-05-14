package hunternif.voxarch.sandbox.castle.builder

import hunternif.voxarch.builder.BuildContext
import hunternif.voxarch.builder.Builder
import hunternif.voxarch.builder.line
import hunternif.voxarch.builder.transformer
import hunternif.voxarch.plan.Room
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.Vec3

/**
 * Inscribe a pyramid into the room. Every section based on the walls.
 * This only builds the surfaces of the pyramid, making it hollow inside.
 */
class PyramidBuilder(
    private val material: String,
    private val upsideDown: Boolean = false
): Builder<Room>() {
    override fun build(node: Room, world: IBlockStorage, context: BuildContext) {
        val apex = Vec3(0.0, node.height, 0.0)
        if (upsideDown) {
            apex.y = 0.0
        }
        node.walls.forEach {
            val p1 = it.origin.clone()
            val p2 = Vec3(it.end.x, 0.0, it.end.z)
            if (upsideDown) {
                p1.y = it.end.y
                p2.y = it.end.y
            }
            buildSection(p1, p2, apex, world, context)
        }
        super.build(node, world, context)
    }

    /**
     * Builds a triangle section of the pyramid.
     * Thanks to theworldfoundry for the line idea
     */
    private fun buildSection(
        p1: Vec3,
        p2: Vec3,
        apex: Vec3,
        world: IBlockStorage,
        context: BuildContext
    ) {
        val transformer = world.transformer()
        line(p1, p2, 0.5) {
            line(it, apex, 1.0, 0.1) { p ->
                val block = context.materials.get(material)
                transformer.setBlock(p.x, p.y, p.z, block)
            }
        }
    }
}