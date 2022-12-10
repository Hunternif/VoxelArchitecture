package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.position
import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    ctx: DomContext,
    private val offset: Vec3,
) : DomBuilder(ctx) {
    override fun build(parentNode: Node) {
        val offset = offset
        children.forEach {
            stylesheet.styleFor(it) {
                position { origin, _ -> origin + offset }
            }
            it.build(parentNode)
        }
    }
}