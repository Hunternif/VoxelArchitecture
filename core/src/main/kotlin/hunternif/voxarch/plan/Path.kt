package hunternif.voxarch.plan

import hunternif.voxarch.vector.Vec3
import java.util.*

/**
 * The points contained in this path are relative to [origin].
 */
open class Path(origin: Vec3) : Node(origin), Collection<Vec3> {
    private val points = LinkedList<Vec3>()

    /** Alternative constructor to add */
    constructor(origin: Vec3, points: List<Vec3>) : this(origin) {
        this.points.addAll(points)
    }

    fun add(point: Vec3) {
        points.add(point)
    }

    fun addAll(points: Collection<Vec3>) {
        this.points.addAll(points)
    }

    override val size: Int get() = points.size

    override fun contains(element: Vec3): Boolean = points.contains(element)

    override fun iterator(): Iterator<Vec3> = points.iterator()

    override fun containsAll(elements: Collection<Vec3>): Boolean =
        points.containsAll(elements)

    override fun isEmpty(): Boolean = points.isEmpty()
}