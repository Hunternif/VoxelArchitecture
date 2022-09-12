package hunternif.voxarch.util

import hunternif.voxarch.storage.IArray3D
import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D
import hunternif.voxarch.vector.IntAABB
import hunternif.voxarch.vector.IntVec3

inline fun <reified T> emptyArray3D() = Array3D<T>(0, 0, 0, emptyArray())

inline fun <T> IStorage3D<T>.forEachPos(
    crossinline action: (IntVec3, T) -> Unit
) {
    for (x in minX .. maxX) {
        for (y in minY .. maxY) {
            for (z in minZ .. maxZ) {
                action(IntVec3(x, y, z), get(x, y, z))
            }
        }
    }
}

inline fun <T> IStorage3D<T>.forEachPos(
    crossinline action: (x: Int, y: Int, z: Int, T) -> Unit
) {
    for (x in minX .. maxX) {
        for (y in minY .. maxY) {
            for (z in minZ .. maxZ) {
                action(x, y, z, get(x, y, z))
            }
        }
    }
}

inline fun IntAABB.forEachXZ(
    crossinline actionXZ: (x: Int, z: Int) -> Unit
) {
    for (x in minX .. maxX) {
        for (z in minZ .. maxZ) {
            actionXZ(x, z)
        }
    }
}

/** Checks if this array has mirror symmetry vs the YZ plane */
fun <T> IArray3D<T>.isSymmetricX(): Boolean {
    for (x in 0 until width/2) {
        for (y in 0 until height) {
            for (z in 0 until length) {
                if (this[x, y, z] != this[width-1-x, y, z]) {
                    return false
                }
            }
        }
    }
    return true
}

/** Checks if this array has mirror symmetry vs the XY plane */
fun <T> IArray3D<T>.isSymmetricZ(): Boolean {
    for (x in 0 until width) {
        for (y in 0 until height) {
            for (z in 0 until length/2) {
                if (this[x, y, z] != this[x, y, length-1-z]) {
                    return false
                }
            }
        }
    }
    return true
}

inline fun <reified T> Array3D<T>.mirrorX(): Array3D<T> =
    Array3D(width, height, length) {
        x, y, z -> this[width-1-x, y, z]
    }

inline fun <reified T> Array3D<T>.mirrorY(): Array3D<T> =
    Array3D(width, height, length) {
        x, y, z -> this[x, height-1-y, z]
    }

inline fun <reified T> Array3D<T>.mirrorZ(): Array3D<T> =
    Array3D(width, height, length) {
        x, y, z -> this[x, y, length-1-z]
    }

/**
 * Creates a new array by rotating this 90 degrees clockwise around the Y axis.
 * Will throw if length < width.
 */
inline fun <reified T> Array3D<T>.rotateY90CW(): Array3D<T> =
    Array3D(width, height, length) {
        x, y, z -> this[z, y, length-1-x]
    }

inline fun <reified T> Array3D<T>.copy(): Array3D<T> =
    Array3D(width, height, length) {
        x, y, z -> this[x, y, z]
    }

/** Copy all data to [dest] at the same coordinates */
inline fun <reified T> IStorage3D<T>.copyTo(dest: IStorage3D<T>) {
    forEachPos { x, y, z, t ->
        dest[x, y, z] = t
    }
}

/** Carves a subarray from this array and returns it.
 * @param from starting point, inclusive.
 * @param size size of the subarray.
 * @return a new array of size `(until-from)`*/
inline fun <reified T> IStorage3D<T>.copySection(
    from: IntVec3,
    size: IntVec3
): Array3D<T> =
    Array3D(size.x, size.y, size.z) {
        x, y, z -> this[from.x + x, from.y + y, from.z + z]
    }

/** Copy data from a given layer along the X axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpXLocal(from: Int = minX) {
    for (x in from + 1 .. maxX)
        for (y in minY .. maxY)
            for (z in minZ .. maxZ)
                this[x, y, z] = this[from, y, z]
}

/** Copy data from a given layer along the X axis towards 0. */
fun <T> IStorage3D<T>.copyDownXLocal(from: Int = maxX) {
    for (x in minX until from)
        for (y in minY .. maxY)
            for (z in minZ .. maxZ)
                this[x, y, z] = this[from, y, z]
}

/** Copy data from a given layer along the Y axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpYLocal(from: Int = minY) {
    for (x in minX .. maxX)
        for (y in from + 1 .. maxY)
            for (z in minZ .. maxZ)
                this[x, y, z] = this[x, from, z]
}

/** Copy data from a given layer along the Y axis towards 0. */
fun <T> IStorage3D<T>.copyDownYLocal(from: Int = maxY) {
    for (x in minX .. maxX)
        for (y in minY until from)
            for (z in minZ .. maxZ)
                this[x, y, z] = this[x, from, z]
}

/** Copy data from a given layer along the Z axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpZLocal(from: Int = minZ) {
    for (x in minX .. maxX)
        for (y in minY .. maxY)
            for (z in from + 1 .. maxZ)
                this[x, y, z] = this[x, y, from]
}

/** Copy data from a given layer along the Z axis towards 0. */
fun <T> IStorage3D<T>.copyDownZLocal(from: Int = maxZ) {
    for (x in minX .. maxX)
        for (y in minY .. maxY)
            for (z in minZ until from)
                this[x, y, z] = this[x, y, from]
}