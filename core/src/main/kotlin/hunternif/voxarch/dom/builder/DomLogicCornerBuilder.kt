package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.round

/**
 * Lays out child elements in every corner of the [PolyRoom]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[PolyRoom]>.
 */
open class DomLogicPolyCornerBuilder : DomBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        val parentNode = ctx.parentNode
        val polygon = when (parentNode) {
            is PolyRoom -> parentNode.polygon
            is Room -> Path().apply {
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
            else -> return children
        }
        return createCornerBuilders(polygon)
    }

    protected fun createCornerBuilders(polygon: Path): List<DomBuilder> =
        polygon.points.mapIndexed { i, offset ->
            val bld = DomTranslateBuilder(offset.round())
            bld.seedOffset = seedOffset + 10000 + i
            bld.children.addAll(children)
            bld
        }
}

/**
 * Lays out child elements in every corner of the [Room]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomLogicFourCornerBuilder : DomLogicPolyCornerBuilder() {
    override fun getChildrenForLayout(ctx: DomBuildContext): Iterable<DomBuilder> {
        val parentNode = ctx.parentNode
        if (parentNode is Room) {
            val polygon = Path().apply {
                rectangle(parentNode.width, parentNode.depth)
            }
            return createCornerBuilders(polygon)
        }
        return children
    }
}