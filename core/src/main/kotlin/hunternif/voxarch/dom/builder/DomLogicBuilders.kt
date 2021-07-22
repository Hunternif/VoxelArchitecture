package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.vector.Vec3

/** Represents elements that don't create nodes but execute some logic. */
abstract class DomLogicBuilder : DomBuilder<Node?>() {
    override val node: Node? = null
}

class DomTranslateBuilder(private val offset: Vec3) : DomLogicBuilder() {
    override fun build(): Node? {
        children.forEach {
            it.build()?.origin?.set(offset)
        }
        return null
    }
}

/** Represents a line segment where a wall could be built. */
class DomLineSegmentBuilder(val p1: Vec3, val p2: Vec3): DomLogicBuilder()

/**
 * Will only work when added as a child to a [DomBuilder]<[PolygonRoom]>.
 * [childBlock] will be executed on every segment of the polygon.
 */
class DomPolygonSegmentBuilder(
    private val childBlock: DomLineSegmentBuilder.() -> Unit
) : DomLogicBuilder() {
    override fun build(): Node? {
        val room = parent.node
        if (room is PolygonRoom) {
            room.polygon.segments.forEachIndexed { i, seg ->
                val bld = DomLineSegmentBuilder(seg.p1, seg.p2)
                addChild(bld, seed + 20000 + i)
                bld.childBlock()
            }
        }
        children.forEach { it.build() }
        return null
    }
}