package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.polygon
import hunternif.voxarch.util.rectangle
import kotlin.math.ceil

open class DomPolygonRoomBuilder : DomNodeBuilder<PolygonRoom>({ PolygonRoom() }) {
    override fun buildNode() = node.createPolygon()
}

internal fun PolygonRoom.createPolygon() {
    polygon.origin = innerFloorCenter
    when (shape) {
        PolygonShape.SQUARE -> polygon.rectangle(width, length)
        PolygonShape.POLYGON -> {
            val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
            polygon.polygon(width, length, sideCount)
        }
        PolygonShape.ROUND -> polygon.ellipse(width, length)
    }
}