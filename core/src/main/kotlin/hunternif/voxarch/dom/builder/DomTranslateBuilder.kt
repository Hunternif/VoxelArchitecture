package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    ctx: DomContext,
    private val offset: Vec3,
) : DomBuilder(ctx) {
    override fun build(parentNode: Node) {
        children.forEach {
            it.build(parentNode)
            // TODO: set offset via style, so that it immediately applies
            if (it is DomNodeBuilder<*>) {
                it.lastNode?.origin?.addLocal(offset)
            }
        }
    }
}