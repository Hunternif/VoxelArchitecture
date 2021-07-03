package hunternif.voxarch.dom

import hunternif.voxarch.plan.Node
import hunternif.voxarch.vector.Vec3

/** Represents elements that don't create nodes but execute some logic. */
abstract class DomLogicBuilder(
    override val parent: DomBuilder<Node?>,
    override val seed: Long
) : DomBuilder<Node?>() {
    override val node: Node? = null
}

class DomTranslateBuilder(
    private val offset: Vec3,
    parent: DomBuilder<Node?>,
    seed: Long
) : DomLogicBuilder(parent, seed) {
    override fun build(): Node? {
        children.forEach {
            it.build()?.origin?.set(offset)
        }
        return null
    }
}