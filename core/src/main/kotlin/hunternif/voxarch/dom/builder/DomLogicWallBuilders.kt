package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.*
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

    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        val dummyWall = ctx.parentNode.wall(p1, p2.addY(ctx.parentNode.height)) {
            transparent = true
        }
        // Using a generic StyledElement avoids calling styles on dummyWall:
        return StyledElement(this, ctx.copy(parentNode = dummyWall))
    }

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        if (children.isEmpty()) return children
        val ctx = children.first().ctx
        val dummyWall = ctx.parentNode
        dummyWall.collapse()
        return children
    }
}

/**
 * Lays out child elements on every segment of the polygon.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom]>.
 */
open class DomPolySegmentBuilder : DomBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        val parentNode = ctx.parentNode
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            else -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
        }
        return createSegmentBuilders(polygon)
    }

    protected fun createSegmentBuilders(polygon: Path): List<DomBuilder> =
        createSegmentBuilders(polygon.origin, polygon.segments)

    protected fun createSegmentBuilders(
        origin: Vec3,
        segments: List<PathSegment>,
    ): List<DomBuilder> = segments.map { seg ->
        val bld = DomLineSegmentBuilder( origin + seg.p1, origin + seg.p2)
        bld.seedOffset = 20000
        bld.children.addAll(children)
        bld
    }
}

/**
 * Lays out child elements on every side of the room.
 *
 * Will work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomFourWallsBuilder : DomPolySegmentBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        val parentNode = ctx.parentNode
        val polygon = Path().apply {
            origin = parentNode.innerFloorCenter
            rectangle(parentNode.width, parentNode.depth)
        }
        return createSegmentBuilders(polygon)
    }
}

/**
 * Lays out child elements on one random segment.
 *
 * Will only work when added as a child to a [DomNodeBuilder] for [Room] or
 * [PolyRoom].
 */
class DomRandomSegmentBuilder : DomPolySegmentBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        val parentNode = ctx.parentNode
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            else -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
        }
        val segment = polygon.segments.random(Random(ctx.seed + seedOffset + 21000))
        return createSegmentBuilders(polygon.origin, listOf(segment))
    }
}
