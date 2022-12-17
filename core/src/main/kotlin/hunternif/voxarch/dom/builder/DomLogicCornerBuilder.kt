package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Path
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.Room
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.util.round

/**
 * Calls [block] in every corner of the [PolyRoom]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[PolyRoom]>.
 */
open class DomLogicPolyCornerBuilder(
    private val block: DomBuilder.() -> Unit
) : DomBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val childCtx = ctx.makeChildCtx()
        if (parentNode is PolyRoom) {
            runCornerBuilders(childCtx, parentNode.polygon)
        }
        children.forEach { it.build(childCtx) }
    }

    protected fun runCornerBuilders(ctx: DomBuildContext, polygon: Path) {
        polygon.points.forEachIndexed { i, offset ->
            val bld = DomTranslateBuilder(offset.round())
            bld.seedOffset = seedOffset + 10000 + i
            bld.block()
            bld.build(ctx)
        }
    }
}

/**
 * Calls [block] in every corner of the [Room]
 *
 * Will only work when added as a child to a [DomNodeBuilder]<[Room]>.
 */
class DomLogicFourCornerBuilder(
    block: DomBuilder.() -> Unit
) : DomLogicPolyCornerBuilder(block) {
    override fun build(ctx: DomBuildContext) = guard {
        val parentNode = ctx.parentNode
        val childCtx = ctx.makeChildCtx()
        if (parentNode is Room) {
            val polygon = Path().apply {
                rectangle(parentNode.width, parentNode.length)
            }
            runCornerBuilders(childCtx, polygon)
        }
        children.forEach { it.build(childCtx) }
    }
}