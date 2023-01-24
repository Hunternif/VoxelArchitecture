package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.pct
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.select
import hunternif.voxarch.dom.style.set
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
    val p1: Vec3, val p2: Vec3,
) : DomBuilder() {
    /** Vector of this segment, from [p1] to [p2] */
    val end: Vec3 = p2.subtract(p1)
    val length: Double = end.length()
    override fun build(ctx: DomBuildContext) = guard {
        val p1 = p1
        val angle = MathUtil.atan2Deg(-end.z, end.x)
        val childCtx = ctx.makeChildCtx()
        children.forEach {
            ctx.stylesheet.add {
                style(select(it).inherit(this@DomLineSegmentBuilder)) {
                    position { origin, _ ->
                        p1.add(origin.rotateY(angle))
                    }
                    width { 100.pct }
                    rotation { set(angle) }
                }
            }
            it.build(childCtx)
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
    private val childBlock: DomLineSegmentBuilder.() -> Unit
) : DomBuilder() {
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
        val childCtx = ctx.makeChildCtx()
        polygon?.let { runSegmentBuilders(childCtx, it.origin, it.segments) }
        children.forEach { it.build(childCtx) }
    }

    protected fun runSegmentBuilders(
        ctx: DomBuildContext,
        origin: Vec3,
        segments: List<PathSegment>
    ) {
        segments.forEachIndexed { i, seg ->
            val bld = DomLineSegmentBuilder( origin + seg.p1, origin + seg.p2)
            bld.seedOffset = seedOffset + 20000 + i
            bld.childBlock()
            bld.build(ctx)
        }
    }
}

/**
 * Calls [childBlock] on every side of the room.
 *
 * Will work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomFourWallsBuilder(
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolySegmentBuilder(childBlock) {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val childCtx = ctx.makeChildCtx()
        if (parentNode is Room) {
            val polygon = Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
            runSegmentBuilders(childCtx, polygon.origin, polygon.segments)
        }
        children.forEach { it.build(childCtx) }
    }
}

/**
 * Calls [childBlock] on one random segment.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom].
 */
class DomRandomSegmentBuilder(
    childBlock: DomLineSegmentBuilder.() -> Unit
) : DomPolySegmentBuilder(childBlock) {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val childCtx = ctx.makeChildCtx()
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
            runSegmentBuilders(childCtx, it.origin, listOf(segment))
        }
        children.forEach { it.build(childCtx) }
    }
}
