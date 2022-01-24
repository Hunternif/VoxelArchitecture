package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.clampMin
import hunternif.voxarch.util.roundToEven
import hunternif.voxarch.util.square
import kotlin.math.PI

/** Castle ward. */
typealias Ward = PolygonRoom

class DomWardBuilder : DomNodeBuilder<Ward>({ newPolygonRoom() }) {
    override fun buildNode() = node.run {
        when (shape) {
            PolygonShape.SQUARE -> polygon.square(width)
            PolygonShape.ROUND -> polygon.circle(width, edgeCount)
        }
    }

    private val edgeCount: Int get() = node.run {
        (width * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
    }
}