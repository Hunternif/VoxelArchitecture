package hunternif.voxarch.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.TransformationStack
import hunternif.voxarch.vector.Vec3

/**
 * Steps along the path and calls [buildAt] at every [step].
 */
open class PathBuilder<in T : Path>(
    val step: Double = 1.0
) : Builder<T>() {
    override fun build(node: T, trans: TransformationStack, world: IBlockStorage, context: BuildContext) {
        // distance traveled along the CURRENT SECTION of the path
        var traveled = 0.0

        // TODO: rotate transformer, and rename to `buildAtX`
        node.segments.forEach { s ->
            line(s.p1, s.p2, step = step, startOffset = traveled) { p ->
                val pos = trans.transform(p)
                buildAt(pos, node, world, context)
                traveled += step
            }
            traveled -= s.length
        }
        super.build(node, trans, world, context)
    }

    /**
     * [point] is relative to origin.
     */
    open fun buildAt(
        point: Vec3,
        node: T,
        world: IBlockStorage,
        context: BuildContext
    ) {}
}