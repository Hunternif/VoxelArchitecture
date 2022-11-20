package hunternif.voxarch.dom.builder

import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(private val offset: Vec3) : DomBuilder() {
    override fun build() {
        children.forEach {
            it.build()
            if (it is DomNodeBuilder<*>) {
                it.node.origin.addLocal(offset)
            }
        }
    }
}