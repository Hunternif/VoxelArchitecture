package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.rectangle
import kotlin.math.ceil

open class DomPolygonRoomBuilder(ctx: DomContext)
    : DomNodeBuilder<PolygonRoom>(ctx, { PolygonRoom() }) {
    override fun buildNode() = node.createPolygon()
}

internal fun PolygonRoom.createPolygon() {
    polygon.origin = innerFloorCenter
    when (shape) {
        PolygonShape.SQUARE -> polygon.rectangle(width, length)
        PolygonShape.ROUND -> {
            val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
            polygon.ellipse(width, length, sideCount)
        }
    }
}