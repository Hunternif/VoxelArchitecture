package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.innerFloorCenter
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
            else -> Path().apply {
                // polygon is assumed to be in the center
                origin = parentNode.innerFloorCenter
                rectangle(parentNode.width, parentNode.depth)
            }
        }
        return createCornerBuilders(polygon)
    }

    protected fun createCornerBuilders(polygon: Path): List<DomBuilder> =
        polygon.points.map { point ->
            // Add origin because points are defined vs polygon origin
            val bld = DomTranslateBuilder((point + polygon.origin).round())
            //TODO: round to global voxels, not local
            bld.seedOffset = 10000
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
        val polygon = Path().apply {
            // polygon is assumed to be in the center
            origin = parentNode.innerFloorCenter
            rectangle(parentNode.width, parentNode.depth)
        }
        return createCornerBuilders(polygon)
    }
}