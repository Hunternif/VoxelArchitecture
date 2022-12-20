package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.rectangle
import kotlin.math.ceil

open class DomPolyRoomBuilder
    : DomNodeBuilder<PolyRoom>(PolyRoom::class.java, { PolyRoom() }) {
    override fun buildNode(ctx: DomBuildContext, node: PolyRoom) =
        node.createPolygon()
}

internal fun PolyRoom.createPolygon() {
    polygon.origin = innerFloorCenter
    when (shape) {
        PolyShape.SQUARE -> polygon.rectangle(length, width)
        PolyShape.ROUND -> {
            val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
            polygon.ellipse(length, width, sideCount)
        }
    }
}