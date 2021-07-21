package hunternif.voxarch.dom

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.square
import kotlin.math.ceil

class DomPolygonRoomBuilder(
    styleClass: Collection<String>,
    parent: DomBuilder<Node?>,
    seed: Long
) : DomNodeBuilder<PolygonRoom>(styleClass, parent, seed, { PolygonRoom() }) {
    override fun build(): PolygonRoom {
        findParentNode().addChild(node)
        stylesheet.apply(this, styleClass)
        createPolygon()
        children.forEach { it.build() }
        return node
    }

    private fun createPolygon() = node.run {
        when (shape) {
            PolygonShape.SQUARE -> polygon.square(width)
            PolygonShape.ROUND -> {
                val sideCount = ceil((size.x + size.z) * 0.167).toInt() * 4
                polygon.circle(width, sideCount)
            }
        }
    }
}