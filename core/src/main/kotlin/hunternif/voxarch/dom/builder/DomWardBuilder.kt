package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.*
import kotlin.math.PI

/** Castle ward. */
class Ward : PolygonRoom()

class DomWardBuilder(ctx: DomContext)
    : DomNodeBuilder<Ward>(ctx, Ward::class.java, { Ward() }) {

    override fun buildNode(node: Ward) = node.run {
        polygon.origin = innerFloorCenter
        when (shape) {
            PolygonShape.SQUARE -> polygon.rectangle(width, length)
            PolygonShape.ROUND -> polygon.ellipse(width, length, edgeCount)
        }
    }

    private val PolygonRoom.edgeCount: Int get() =
        ((width + length) / 2 * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
}