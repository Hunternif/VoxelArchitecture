package hunternif.voxarch.dom

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

class DomWardBuilder(
    styleClass: Array<out String>,
    parent: DomBuilder<Node?>,
    seed: Long
) : DomNodeBuilder<Ward>(styleClass, parent, seed, { Ward() }) {

    internal var allCornerBuild: DomBuilder<Node?>.() -> Unit = {}

    override fun build(): Ward {
        findParentNode().addChild(node)
        root.stylesheet.apply(this, styleClass)
        createPolygon()
        buildCorners()
        children.forEach { it.build() }
        return node
    }

    private fun createPolygon() = node.run {
        when (shape) {
            PolygonShape.SQUARE -> polygon.square(width)
            PolygonShape.ROUND -> polygon.circle(width, edgeCount)
        }
    }

    private fun buildCorners() {
        node.polygon.points.forEachIndexed { i, offset ->
            val bld = DomTranslateBuilder(offset, this, seed + 10000 + i)
            children.add(bld)
            bld.allCornerBuild()
        }
    }

    private val edgeCount: Int get() = node.run {
        (width * PI / edgeLength)
            .clampMin(4.0).roundToEven().toInt()
    }
}