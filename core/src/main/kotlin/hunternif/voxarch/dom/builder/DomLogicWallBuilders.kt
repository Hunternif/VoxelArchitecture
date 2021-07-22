package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.Vec3

/** Represents a line segment where a wall could be built. */
class DomLineSegmentBuilder(val p1: Vec3, val p2: Vec3) : DomLogicBuilder()

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
            addSegmentBuilders(room.polygon)
        }
        children.forEach { it.build() }
        return null
    }

    protected fun addSegmentBuilders(polygon: Path) {
        polygon.segments.forEachIndexed { i, seg ->
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
    private val childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolygonSegmentBuilder(childBlock) {
    override fun build(): Node? {
        val room = parent.node
        if (room is Room) {
            val polygon = Path().apply {
                rectangle(room.width, room.length)
            }
            addSegmentBuilders(polygon)
        }
        children.forEach { it.build() }
        return null
    }
}
