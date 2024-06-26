package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.vector.Vec3

/**
 * During layout, moves children's origins by [offset]
 */
class DomTranslateBuilder(
    private val offset: Vec3,
) : DomBuilder() {

    override fun layout(children: List<StyledElement<*>>): List<StyledElement<*>> {
        children.filterIsInstance<StyledNode<*>>().forEach {
            it.node.origin.addLocal(offset)
        }
        return children
    }
}