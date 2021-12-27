package hunternif.voxarch.editor.util

import hunternif.voxarch.vector.IntVec3
import org.joml.Vector3f

fun IntVec3.toVector3f() = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())