package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.util.*
import kotlin.math.PI

/** Castle ward. */
class Ward : PolyRoom()

class DomWardBuilder(ctx: DomContext)
    : DomNodeBuilder<Ward>(ctx, Ward::class.java, { Ward() }) {

    override fun buildNode(node: Ward) = node.run {
        polygon.origin = innerFloorCenter
        when (shape) {
            PolyShape.SQUARE -> polygon.rectangle(width, length)
            PolyShape.ROUND -> polygon.ellipse(width, length, edgeCount)
        }
    }

    private val PolyRoom.edgeCount: Int get() =
        ((width + length) / 2 * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
}