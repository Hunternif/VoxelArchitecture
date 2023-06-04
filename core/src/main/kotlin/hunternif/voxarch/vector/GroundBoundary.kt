package hunternif.voxarch.vector

/**
 * Contains a horizontal line segment and also a vertical plane that runs through it.
 */
class GroundBoundary(val start: Vec3, val end: Vec3) : Plane() {
    init {
        set(vertical(start, end))
    }

    val length: Double = end.distanceTo(start)

    /**
     * Moves the plane by vector [delta]. I.e. if it passed through point `A`,
     * now it will pass through point `A + delta`.
     * Returns a new plane.
     */
    override fun move(delta: Vec3): GroundBoundary =
        GroundBoundary(start + delta, end + delta)

    /** Applies the given transformation to this plane and returns a new plane. */
    override fun transform(trans: ILinearTransformation): GroundBoundary =
        GroundBoundary(trans.transform(start), trans.transform(end))
}