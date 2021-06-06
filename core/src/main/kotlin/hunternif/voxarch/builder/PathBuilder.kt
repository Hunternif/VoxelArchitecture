package hunternif.voxarch.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.storage.IBlockStorage
import hunternif.voxarch.vector.Vec3

/**
 * Steps along the path and calls [buildAt] at every [step].
 */
open class PathBuilder<in T : Path>(
    val step: Double = 1.0
) : Builder<T>() {
    override fun build(node: T, world: IBlockStorage, context: BuildContext) {
        // distance traveled along the CURRENT SECTION of the path
        var traveled = 0.0

        node.zipWithNext { p1, p2 ->
            line(p1, p2, step = step, startOffset = traveled) { p ->
                buildAt(p, node, world, context)
                traveled += step
            }
            traveled -= p1.distanceTo(p2)
        }
        super.build(node, world, context)
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