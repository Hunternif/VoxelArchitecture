package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3

data class PathSegment(
    val p1: Vec3,
    val p2: Vec3,
    val length: Double,
    val distanceFromStart: Double
)

/**
 * The points contained in this path are relative to [origin].
 */
open class Path(origin: Vec3) : Node(origin) {
    private val _points = mutableListOf<Vec3>()
    val points: List<Vec3>
        get() = _points

    private val _segments = mutableListOf<PathSegment>()
    val segments: List<PathSegment>
        get() = _segments

    /** Alternative constructor to add */
    constructor(origin: Vec3, points: List<Vec3>) : this(origin) {
        points.forEach { addPoint(it) }
    }

    fun addPoint(point: Vec3) {
        points.lastOrNull()?.let {
            val distance = _segments.lastOrNull()?.run {
                distanceFromStart + length
            } ?: 0.0
            _segments.add(PathSegment(
                it, point, point.distanceTo(it), distance
            ))
        }
        _points.add(point)
    }

    /** Connects the end of the path to the start */
    fun loopToStart() {
        points.firstOrNull()?.let {
            addPoint(it)
        }
    }

    /**
     * Returns the segment at which you arrive if you walk distance [x]
     * along the path from the first point along the path.
     * Returns null if [x] exceeds total distance.
     */
    fun mapX(x: Double): PathSegment? {
        if (x < 0 || x > totalLength) return null
        val i = _segments.binarySearch { it.distanceFromStart.compareTo(x) }
        val index = if (i < 0) -2-i else i
        if (index < 0 || index >= _segments.size) return null
        return _segments[index]
    }

    /** Total length of all segments combined. */
    val totalLength: Double
        get() = _segments.lastOrNull()?.run {
            distanceFromStart + length
        } ?: 0.0

    constructor(): this(Vec3.ZERO)
}