package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.plan.innerFloorCenter
import hunternif.voxarch.util.*
import kotlin.math.PI

/** Castle ward. */
class Ward : PolyRoom()

class DomWardBuilder : DomNodeBuilder<Ward>(Ward::class.java, { Ward() }) {

    override fun postLayout(element: StyledNode<Ward>) = element.node.run {
        polygon.position = innerFloorCenter
        when (shape) {
            PolyShape.SQUARE -> polygon.rectangle(width, depth)
            PolyShape.ROUND -> polygon.ellipse(width, depth, edgeCount)
        }
    }

    private val PolyRoom.edgeCount: Int get() =
        ((width + depth) / 2 * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
}