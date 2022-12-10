package hunternif.voxarch.dom.builder

import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    ctx: DomContext,
    private val offset: Vec3,
) : DomBuilder(ctx) {
    override fun build() {
        children.forEach {
            it.build()
            if (it is DomNodeBuilder<*>) {
                it.node.origin.addLocal(offset)
            }
        }
    }
}