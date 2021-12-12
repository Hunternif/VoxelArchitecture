package hunternif.voxarch.wfc.tiled

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.util.*
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.wfc.WfcColor

/** These tiles are matched voxel-for-voxel. */
class WangTile(
    internal val data: Array3D<WfcColor>,
    override var probability: Double = 1.0
): WfcCachingTile(), IStorage3D<WfcColor> by data {
    constructor (
        width: Int,
        height: Int,
        length: Int,
        init: (x: Int, y: Int, z: Int) -> WfcColor
    ) : this(Array3D(width, height, length, init))

    constructor(width: Int, height: Int, length: Int, vx: WfcColor):
        this(width, height, length, { _, _, _ -> vx })

    fun mirrorX() = WangTile(data.mirrorX(), probability)
    fun mirrorY() = WangTile(data.mirrorY(), probability)
    fun mirrorZ() = WangTile(data.mirrorZ(), probability)
    fun rotateY90CW() = WangTile(data.rotateY90CW(), probability)
    fun copy() = WangTile(data.copy(), probability)
    /** Creates a tile with the same data and the given probability. */
    operator fun rem(probability: Number) = WangTile(data, probability.toDouble())

    /** Returns 4 rotations of this tile around the Y axis */
    fun generateRotationsY(): List<WangTile> {
        if (isSymmetricX() && isSymmetricZ()) {
            val t1 = this % (probability / 2f)
            return listOf(t1, t1.rotateY90CW())
        }
        val t1 = this % (probability / 4f)
        val t2 = t1.rotateY90CW()
        val t3 = t2.rotateY90CW()
        val t4 = t3.rotateY90CW()
        return listOf(t1, t2, t3, t4)
    }

    override fun calculateMatch(other: WfcTile, dir: Direction3D): Boolean {
        if (other !is WangTile) return false
        when(dir) {
            UP -> {
                if (other.width != width || other.length != length) return false
                for (x in 0 until width) {
                    for (z in 0 until length) {
                        if (this[x, height-1, z] != other[x, 0, z]) return false
                    }
                }
                return true
            }
            DOWN -> {
                if (other.width != width || other.length != length) return false
                for (x in 0 until width) {
                    for (z in 0 until length) {
                        if (this[x, 0, z] != other[x, other.height-1, z]) return false
                    }
                }
                return true
            }
            EAST -> {
                if (other.height != height || other.length != length) return false
                for (y in 0 until height) {
                    for (z in 0 until length) {
                        if (this[width-1, y, z] != other[0, y, z]) return false
                    }
                }
                return true
            }
            SOUTH -> {
                if (other.height != height || other.width != width) return false
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        if (this[x, y, length-1] != other[x, y, 0]) return false
                    }
                }
                return true
            }
            WEST -> {
                if (other.height != height || other.length != length) return false
                for (y in 0 until height) {
                    for (z in 0 until length) {
                        if (this[0, y, z] != other[other.width-1, y, z]) return false
                    }
                }
                return true
            }
            NORTH -> {
                if (other.height != height || other.width != width) return false
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        if (this[x, y, 0] != other[x, y, other.length-1]) return false
                    }
                }
                return true
            }
        }
    }
}