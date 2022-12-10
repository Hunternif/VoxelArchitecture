package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.round

/**
 * Calls [block] in every corner of the [PolygonRoom]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[PolygonRoom]>.
 */
open class DomLogicPolygonCornerBuilder(
    ctx: DomContext,
    private val block: DomBuilder.() -> Unit
) : DomBuilder(ctx) {
    override fun build(parentNode: Node) {
        if (parentNode is PolygonRoom) {
            addCornerBuilders(parentNode.polygon)
        }
        children.forEach { it.build(parentNode) }
    }

    protected fun addCornerBuilders(polygon: Path) {
        polygon.points.forEachIndexed { i, offset ->
            val bld = DomTranslateBuilder(ctx, offset.round())
            addChild(bld, seed + 10000 + i)
            bld.block()
        }
    }
}

/**
 * Calls [block] in every corner of the [Room]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomLogicFourCornerBuilder(
    ctx: DomContext,
    block: DomBuilder.() -> Unit
) : DomLogicPolygonCornerBuilder(ctx, block) {
    override fun build(parentNode: Node) {
        if (parentNode is Room) {
            val polygon = Path().apply {
                rectangle(parentNode.width, parentNode.length)
            }
            addCornerBuilders(polygon)
        }
        children.forEach { it.build(parentNode) }
    }
}