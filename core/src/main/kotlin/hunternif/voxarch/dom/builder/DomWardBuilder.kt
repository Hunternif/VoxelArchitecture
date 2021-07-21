package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape
import hunternif.voxarch.util.circle
import hunternif.voxarch.util.clampMin
import hunternif.voxarch.util.roundToEven
import hunternif.voxarch.util.square
import kotlin.math.PI

/** Castle ward. */
class Ward : PolygonRoom()

class DomWardBuilder : DomNodeBuilder<Ward>({ Ward() }) {

    internal var allCornerBuild: DomBuilder<Node?>.() -> Unit = {}

    override fun buildNode() {
        createPolygon()
        buildCorners()
    }

    private fun createPolygon() = node.run {
        when (shape) {
            PolygonShape.SQUARE -> polygon.square(width)
            PolygonShape.ROUND -> polygon.circle(width, edgeCount)
        }
    }

    private fun buildCorners() {
        node.polygon.points.forEachIndexed { i, offset ->
            val bld = DomTranslateBuilder(offset)
            addChild(bld, seed + 10000 + i)
            bld.allCornerBuild()
        }
    }

    private val edgeCount: Int get() = node.run {
        (width * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
    }
}