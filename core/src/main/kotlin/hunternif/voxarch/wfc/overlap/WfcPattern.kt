package hunternif.voxarch.wfc.overlap

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.util.IRandomOption
import hunternif.voxarch.vector.Array3D

/**
 * NxKxN pattern.
 * Using potentially different size for Y because patterns will be rotated only
 * around the Y axis, i.e. we don't want upside-down houses.
 */
open class WfcPattern<C>(
    internal val data: Array3D<C>,
    override var probability: Double = 1.0
) : IRandomOption, IArray3D<C> by data {
    companion object {
        inline operator fun <reified C> invoke(
            width: Int,
            height: Int,
            depth: Int,
            init: C
        ) = WfcPattern(Array3D(width, height, depth, init))
        inline operator fun <reified C> invoke(
            width: Int,
            height: Int,
            depth: Int,
            init: (x: Int, y: Int, z: Int) -> C
        ) = WfcPattern(Array3D(width, height, depth, init))
    }
}