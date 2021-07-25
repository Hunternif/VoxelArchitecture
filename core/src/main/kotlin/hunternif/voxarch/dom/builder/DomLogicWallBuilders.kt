package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.MathUtil
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3
import kotlin.random.Random

/**
 * Represents a line segment where a wall could be built.
 * [p1] (start) and [p2] (end) are defined relative to parent's origin.
 * Children will be translated to [p1] and rotated so that X axis runs along
 * [p1]-[p2].
 */
class DomLineSegmentBuilder(val p1: Vec3, val p2: Vec3): DomLogicBuilder() {
    val end: Vec3 = p2.subtract(p1)
    override fun build(): Node? {
        children.forEach {
            it.build()?.apply {
                //TODO: add node rotation?
                val angle = MathUtil.atan2Deg(-end.z, end.x)
                origin = p1.add(origin.rotateY(angle))
            }
        }
        return null
    }
}

/**
 * Calls [childBlock] on every segment of the polygon.
 *
 * Will only work when added as a child to a [DomBuilder]<[PolygonRoom]>.
 */
open class DomPolygonSegmentBuilder(
    private val childBlock: DomLineSegmentBuilder.() -> Unit
) : DomLogicBuilder() {
    override fun build(): Node? {
        val room = parent.node
        if (room is PolygonRoom) {
            addSegmentBuilders(room.polygon.segments)
        }
        children.forEach { it.build() }
        return null
    }

    protected fun addSegmentBuilders(segments: List<PathSegment>) {
        segments.forEachIndexed { i, seg ->
            val bld = DomLineSegmentBuilder(seg.p1, seg.p2)
            addChild(bld, seed + 20000 + i)
            bld.childBlock()
        }
    }
}

/**
 * Calls [childBlock] on every side of the room.
 *
 * Will work when added as a child to a [DomBuilder]<[Room]>.
 */
class DomFourWallsBuilder(
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolygonSegmentBuilder(childBlock) {
    override fun build(): Node? {
        val room = parent.node
        if (room is Room) {
            val polygon = Path().apply {
                rectangle(room.width, room.length)
            }
            addSegmentBuilders(polygon.segments)
        }
        children.forEach { it.build() }
        return null
    }
}

/**
 * Calls [childBlock] on one random segment.
 *
 * Will work when added as a child to a [DomBuilder]<[PolygonRoom]>.
 */
class DomRandomSegmentBuilder(
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolygonSegmentBuilder(childBlock) {
    override fun build(): Node? {
        val room = parent.node
        if (room is PolygonRoom) {
            val segment = room.polygon.segments
                .random(Random(seed + 21000))
            addSegmentBuilders(listOf(segment))
        }
        children.forEach { it.build() }
        return null
    }
}
