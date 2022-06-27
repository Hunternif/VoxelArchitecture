package hunternif.voxarch.vector


/** A generic interface for coordinate transformations. */
interface ITransformation {
    /** Transforms THIS vector and returns it. */
    fun transform(vec: Vec3): Vec3


    // Convenience methods
    /** Transforms the given coordinates and returns a new vector. */
    fun transform(x: Double, y: Double, z: Double): Vec3 = transform(Vec3(x, y, z))

    /** Transforms the given coordinates and returns a new vector. */
    fun transform(x: Number, y: Number, z: Number): Vec3 =
        transform(x.toDouble(), y.toDouble(), z.toDouble())
}