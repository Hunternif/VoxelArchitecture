package hunternif.voxarch.vector

/**
 * A linear transformation of coordinates, expressed in a matrix.
 * This is meant to be used when traversing the node graph.
 */
interface ILinearTransformation : ITransformation {
    /** Angle of counterclockwise rotation. Need to remember it to rotate blocks correctly. */
    val angleY: Double

    /** Transformation matrix. */
    val matrix: Matrix4

    /** Modifies this transformation, adds translation, returns this. */
    fun translate(x: Double, y: Double, z: Double): ILinearTransformation

    /** Modifies this transformation, adds rotation counterclockwise around the Y axis, returns this. */
    fun rotateY(angle: Double): ILinearTransformation


    // Convenience methods
    /** Modifies this transformation, adds translation, returns this. */
    fun translate(vec: Vec3): ILinearTransformation = translate(vec.x, vec.y, vec.z)

    /** Modifies this transformation, adds translation, returns this. */
    fun translate(x: Number, y: Number, z: Number): ILinearTransformation =
        translate(x.toDouble(), y.toDouble(), z.toDouble())
}


class LinearTransformation(
    override var angleY: Double = 0.0,
    override var matrix: Matrix4 = Matrix4.identity()
) : ILinearTransformation {
    /** Saving memory by reusing the same vector.  */
    private val vec4 = Vec4(0, 0, 0, 1)

    override fun transform(vec: Vec3): Vec3 {
        vec4.set(vec.x, vec.y, vec.z, 1.0)
        matrix.multiplyLocal(vec4)
        vec.set(vec4.x, vec4.y, vec4.z)
        return vec
    }

    override fun translate(x: Double, y: Double, z: Double): LinearTransformation {
        matrix.multiplyLocal(Matrix4.translation(x, y, z))
        return this
    }

    override fun rotateY(angle: Double): LinearTransformation {
        angleY += angle
        matrix.multiplyLocal(Matrix4.rotationY(angle))
        return this
    }

    fun clone() = LinearTransformation(angleY, matrix.clone())
}