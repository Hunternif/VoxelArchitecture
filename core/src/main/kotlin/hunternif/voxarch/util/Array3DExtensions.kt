package hunternif.voxarch.util

import hunternif.voxarch.storage.IStorage3D
import hunternif.voxarch.vector.Array3D

/** Checks if this array has mirror symmetry vs the YZ plane */
fun <T> IStorage3D<T>.isSymmetricX(): Boolean {
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
fun <T> IStorage3D<T>.isSymmetricZ(): Boolean {
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

/** Copy data from a given layer along the X axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpXLocal(from: Int = 0) {
    for (x in from + 1 until width)
        for (y in 0 until height)
            for (z in 0 until length)
                this[x, y, z] = this[from, y, z]
}

/** Copy data from a given layer along the X axis towards 0. */
fun <T> IStorage3D<T>.copyDownXLocal(from: Int = width - 1) {
    for (x in 0 until from)
        for (y in 0 until height)
            for (z in 0 until length)
                this[x, y, z] = this[from, y, z]
}

/** Copy data from a given layer along the Y axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpYLocal(from: Int = 0) {
    for (x in 0 until width)
        for (y in from + 1 until height)
            for (z in 0 until length)
                this[x, y, z] = this[x, from, z]
}

/** Copy data from a given layer along the Y axis towards 0. */
fun <T> IStorage3D<T>.copyDownYLocal(from: Int = height - 1) {
    for (x in 0 until width)
        for (y in 0 until from)
            for (z in 0 until length)
                this[x, y, z] = this[x, from, z]
}

/** Copy data from a given layer along the Z axis towards positive numbers. */
fun <T> IStorage3D<T>.copyUpZLocal(from: Int = 0) {
    for (x in 0 until width)
        for (y in 0 until height)
            for (z in from + 1 until length)
                this[x, y, z] = this[x, y, from]
}

/** Copy data from a given layer along the Z axis towards 0. */
fun <T> IStorage3D<T>.copyDownZLocal(from: Int = length - 1) {
    for (x in 0 until width)
        for (y in 0 until height)
            for (z in 0 until from)
                this[x, y, z] = this[x, y, from]
}