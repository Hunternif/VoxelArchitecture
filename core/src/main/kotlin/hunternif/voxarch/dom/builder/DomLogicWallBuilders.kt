package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.position
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
class DomLineSegmentBuilder(
    ctx: DomContext,
    val p1: Vec3, val p2: Vec3,
) : DomBuilder(ctx) {
    /** Vector of this segment, from [p1] to [p2] */
    val end: Vec3 = p2.subtract(p1)
    override fun build(parentNode: Node) {
        val p1 = p1
        val angle = MathUtil.atan2Deg(-end.z, end.x)
        children.forEach {
            stylesheet.styleFor(it) {
                //TODO: add node rotation?
                position { origin, _ ->
                    p1.add(origin.rotateY(angle))
                }
            }
            it.build(parentNode)
        }
    }
}

/**
 * Calls [childBlock] on every segment of the polygon.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom]>.
 */
open class DomPolySegmentBuilder(
    ctx: DomContext,
    private val childBlock: DomLineSegmentBuilder.() -> Unit
) : DomBuilder(ctx) {
    override fun build(parentNode: Node) {
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            is Room -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.length)
            }
            else -> null
        }
        polygon?.let { addSegmentBuilders(it.origin, it.segments) }
        children.forEach { it.build(parentNode) }
    }

    protected fun addSegmentBuilders(origin: Vec3, segments: List<PathSegment>) {
        segments.forEachIndexed { i, seg ->
            val bld = DomLineSegmentBuilder(ctx, origin + seg.p1, origin + seg.p2)
            addChild(bld, seed + 20000 + i)
            bld.childBlock()
        }
    }
}

/**
 * Calls [childBlock] on every side of the room.
 *
 * Will work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomFourWallsBuilder(
    ctx: DomContext,
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolySegmentBuilder(ctx, childBlock) {
    override fun build(parentNode: Node) {
        if (parentNode is Room) {
            val polygon = Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.length)
            }
            addSegmentBuilders(polygon.origin, polygon.segments)
        }
        children.forEach { it.build(parentNode) }
    }
}

/**
 * Calls [childBlock] on one random segment.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom].
 */
class DomRandomSegmentBuilder(
    ctx: DomContext,
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolySegmentBuilder(ctx, childBlock) {
    override fun build(parentNode: Node) {
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            is Room -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.length)
            }
            else -> null
        }
        polygon?.let {
            val segment = it.segments.random(Random(seed + 21000))
            addSegmentBuilders(it.origin, listOf(segment))
        }
        children.forEach { it.build(parentNode) }
    }
}
