package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.vector.Vec3

/**
 * During layout, sets children's origins to [position]
 */
class DomPositionBuilder(
    private val position: Vec3,
) : DomBuilder() {

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        children.filterIsInstance<StyledNode<*>>().forEach {
            it.node.origin.set(position)
        }
        return children
    }
}