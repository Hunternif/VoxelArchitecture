package hunternif.voxarch.editor.util

import hunternif.voxarch.vector.IntVec3
import org.joml.Vector3f
import org.joml.Vector3i
import kotlin.math.floor
import kotlin.math.ceil
import kotlin.math.round

fun IntVec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())

fun Vector3f.toIntFloor() = Vector3i(floor(x).toInt(), floor(y).toInt(), floor(z).toInt())
fun Vector3f.toIntCeil() = Vector3i(ceil(x).toInt(), ceil(y).toInt(), ceil(z).toInt())
/** Assuming that voxels are rendered at (x-0.5, y-0.5, z-0.5) */
fun Vector3f.toVoxCoords() = Vector3i(
    round(x).toInt(),
    round(y).toInt(),
    round(z).toInt()
)