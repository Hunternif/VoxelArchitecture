package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.*
import kotlin.math.PI

/** Castle ward. */
class Ward : PolygonRoom()

class DomWardBuilder : DomNodeBuilder<Ward>({ Ward() }) {
    override fun buildNode() = node.run {
        polygon.origin = innerFloorCenter
        when (shape) {
            PolygonShape.SQUARE -> polygon.rectangle(width, length)
            PolygonShape.POLYGON -> polygon.polygon(width, length, polygonEdgeCount)
            PolygonShape.ROUND -> polygon.ellipse(width, length)
        }
    }

    private val polygonEdgeCount: Int get() = node.run {
        ((width + length) / 2 * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
    }
}