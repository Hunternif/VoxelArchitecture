package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.node
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    private val offset: Vec3,
) : DomBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val offsetNode = ctx.parentNode.node(offset)
        val childCtx = ctx.copy(this, offsetNode).inherit(styleClass)
        children.forEach { it.build(childCtx) }
    }
}