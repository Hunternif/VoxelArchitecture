package hunternif.voxarch.editor.util

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f
import org.joml.Vector3i
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.round

fun IntVec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3i.toVec3() = Vec3(x, y, z)
fun Vec3.toVector3i() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3f.toIntFloor() = Vector3i(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
fun Vector3f.toIntCeil() = Vector3i(ceil(x).toInt(), ceil(y).toInt(), ceil(z).toInt())
/** Assuming that voxels are rendered at (x-0.5, y-0.5, z-0.5), and that
 * the current vector is at voxel floor level (y=-0.5) */
fun Vector3f.fromFloorToVoxCoords() = Vector3i(
    round(x).toInt(),
    round(y + 0.5f).toInt(),
    round(z).toInt()
)

data class Vertex(
    val pos: Vector3f,
    val normal: Vector3f,
) {
    constructor(
        posX: Float,
        posY: Float,
        posZ: Float,
        normalX: Float,
        normalY: Float,
        normalZ: Float
    ) : this(Vector3f(posX, posY, posZ), Vector3f(normalX, normalY, normalZ))
}

data class Edge(
    val start: Vector3f,
    val end: Vector3f,
)