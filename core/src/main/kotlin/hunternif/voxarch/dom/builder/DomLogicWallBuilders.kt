package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.Vec3
import kotlin.random.Random

/**
 * Represents a line segment where a wall could be built.
 * [p1] (start) and [p2] (end) are defined relative to parent's origin.
 * Children will be translated to [p1] and rotated so that X axis runs along
 * [p1]-[p2].
 */
class DomLineSegmentBuilder(
    val p1: Vec3, val p2: Vec3,
) : DomBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val dummyWall = ctx.parentNode.wall(p1, p2.addY(ctx.parentNode.height)) {
            transparent = true
        }
        val childCtx = ctx.copy(this, dummyWall).inherit(styleClass)
        children.forEach { it.build(childCtx) }
    }
}

/**
 * Executes child elements on every segment of the polygon.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom]>.
 */
open class DomPolySegmentBuilder : DomBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            is Room -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
            else -> null
        }
        polygon?.let { runSegmentBuilders(ctx, it.origin, it.segments) }
    }

    protected fun runSegmentBuilders(
        ctx: DomBuildContext,
        origin: Vec3,
        segments: List<PathSegment>
    ) {
        segments.forEachIndexed { i, seg ->
            val bld = DomLineSegmentBuilder( origin + seg.p1, origin + seg.p2)
            bld.seedOffset = seedOffset + 20000 + i
            bld.children.addAll(children)
            bld.build(ctx)
        }
    }
}

/**
 * Executes child elements on every side of the room.
 *
 * Will work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomFourWallsBuilder : DomPolySegmentBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        if (parentNode is Room) {
            val polygon = Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
            runSegmentBuilders(ctx, polygon.origin, polygon.segments)
        }
    }
}

/**
 * Executes child elements on one random segment.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom].
 */
class DomRandomSegmentBuilder : DomPolySegmentBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            is Room -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
            else -> null
        }
        polygon?.let {
            val segment = it.segments.random(Random(ctx.seed + seedOffset + 21000))
            runSegmentBuilders(ctx, it.origin, listOf(segment))
        }
    }
}
