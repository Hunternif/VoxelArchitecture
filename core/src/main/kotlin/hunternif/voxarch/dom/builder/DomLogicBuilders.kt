package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
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