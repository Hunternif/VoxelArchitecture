package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.util.*
import kotlin.math.PI

/** Castle ward. */
class Ward : PolyRoom()

class DomWardBuilder : DomNodeBuilder<Ward>(Ward::class.java, { Ward() }) {

    override fun buildNode(ctx: DomBuildContext, node: Ward) = node.run {
        polygon.origin = innerFloorCenter
        when (shape) {
            PolyShape.SQUARE -> polygon.rectangle(width, depth)
            PolyShape.ROUND -> polygon.ellipse(width, depth, edgeCount)
        }
    }

    private val PolyRoom.edgeCount: Int get() =
        ((width + depth) / 2 * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
}