package hunternif.voxarch.vector


/**
 * A generic interface for coordinate transformations.
 * Takes local coordinates as arguments and translates them into global coordinates.
 */
interface ITransformation {
    /** Transforms THIS vector and returns it. */
    fun transformLocal(vec: Vec3): Vec3


    // Convenience methods
    /** Transforms the given vector and returns a new vector. */
    fun transform(vec: Vec3): Vec3 = transformLocal(Vec3(vec))
    /** Transforms the given vector and returns a new vector. */
    fun transform(vec: IntVec3): Vec3 = transformLocal(Vec3(vec))
    /** Transforms the given coordinates and returns a new vector. */
    fun transform(x: Double, y: Double, z: Double): Vec3 = transformLocal(Vec3(x, y, z))

    /** Transforms the given coordinates and returns a new vector. */
    fun transform(x: Number, y: Number, z: Number): Vec3 =
        transform(x.toDouble(), y.toDouble(), z.toDouble())
}