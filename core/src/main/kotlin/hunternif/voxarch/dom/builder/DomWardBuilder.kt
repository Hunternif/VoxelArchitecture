package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom

/** Castle ward. */
class Ward : PolyRoom()

class DomWardBuilder : DomNodeBuilder<Ward>(Ward::class.java, { Ward() }) {

    @Deprecated("use PolyRoom instead")
    override fun postLayout(element: StyledNode<Ward>) = element.node.run {
        element.node.createPolygon()
    }
}