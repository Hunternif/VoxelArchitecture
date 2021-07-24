package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.square
import kotlin.math.ceil

class DomPolygonRoomBuilder : DomNodeBuilder<PolygonRoom>({ PolygonRoom() }) {
    override fun buildNode() = node.run {
        when (shape) {
            PolygonShape.SQUARE -> polygon.square(width)
            PolygonShape.ROUND -> {
                val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
                polygon.circle(width, sideCount)
            }
        }
    }
}