package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledNode
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape
import hunternif.voxarch.plan.innerFloorCenter
import hunternif.voxarch.util.ellipse
import hunternif.voxarch.util.rectangle
import kotlin.math.ceil

open class DomPolyRoomBuilder
    : DomNodeBuilder<PolyRoom>(PolyRoom::class.java, { PolyRoom() }) {
    override fun postLayout(element: StyledNode<PolyRoom>) =
        element.node.createPolygon()
}

internal fun PolyRoom.createPolygon() {
    polygon.position = innerFloorCenter
    when (shape) {
        PolyShape.SQUARE -> polygon.rectangle(width, depth)
        PolyShape.ROUND -> {
            val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
            polygon.ellipse(width, depth, sideCount)
        }
    }
}