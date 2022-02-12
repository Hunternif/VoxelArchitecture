package hunternif.voxarch.editor.util

import hunternif.voxarch.vector.IntVec3
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector3i
import kotlin.math.*

fun IntVec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3i.toVec3() = Vec3(x, y, z)
fun Vector3f.toVec3() = Vec3(x, y, z)
fun Vec3.toVector3i() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3f.set(v: Vec3): Vector3f = set(v.x, v.y, v.z)
fun Vector3f.add(v: Vec3): Vector3f = add(v.x.toFloat(), v.y.toFloat(), v.z.toFloat())
fun Vector3f.sub(v: Vec3): Vector3f = sub(v.x.toFloat(), v.y.toFloat(), v.z.toFloat())

fun Vec3.writeToFloatArray(array: FloatArray) {
    array[0] = x.toFloat()
    array[1] = y.toFloat()
    array[2] = z.toFloat()
}

fun Vec3.readFromFloatArray(array: FloatArray) {
    x = array[0].toDouble()
    y = array[1].toDouble()
    z = array[2].toDouble()
}

fun min(a: Vector3i, b: Vector3i) = Vector3i(
    min(a.x, b.x),
    min(a.y, b.y),
    min(a.z, b.z),
)
fun min(a: Vector3f, b: Vector3f) = Vector3f(
    min(a.x, b.x),
    min(a.y, b.y),
    min(a.z, b.z),
)
fun max(a: Vector3i, b: Vector3i) = Vector3i(
    max(a.x, b.x),
    max(a.y, b.y),
    max(a.z, b.z),
)
fun max(a: Vector3f, b: Vector3f) = Vector3f(
    max(a.x, b.x),
    max(a.y, b.y),
    max(a.z, b.z),
)

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

enum class AADirection3D(val vec: Vector3fc) {
    POS_X(Vector3f(1f, 0f, 0f)),
    POS_Y(Vector3f(0f, 1f, 0f)),
    POS_Z(Vector3f(0f, 0f, 1f)),
    NEG_X(Vector3f(-1f, 0f, 0f)),
    NEG_Y(Vector3f(0f, -1f, 0f)),
    NEG_Z(Vector3f(0f, 0f, -1f))
}

/**
 * Quad face of an AABB.
 * [min] & [max] define the AABB with additional width to allow hit-testing.
 * [vertices] are the 4 vertices in corners of the flat face.
 */
class AABBFace(
    val dir: AADirection3D,
    val min: Vector3f,
    val max: Vector3f,
    val vertices: Array<out Vector3f>,
) {
    constructor(
        dir: AADirection3D,
        minX: Float,
        minY: Float,
        minZ: Float,
        maxX: Float,
        maxY: Float,
        maxZ: Float,
        vararg vertices: Vector3f,
    ) : this(
        dir,
        Vector3f(minX, minY, minZ),
        Vector3f(maxX, maxY, maxZ),
        vertices,
    )
}