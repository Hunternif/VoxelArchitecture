package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.Vec3

class DomPathBuilder : DomNodeBuilder<Path>(Path::class.java, { Path() }) {
    override fun buildNode(ctx: DomBuildContext, node: Path) {
        // match the shape of parent
        val p = ctx.parentNode
        when (p) {
            is PolyRoom -> node.addPoints(p.polygon.points)
            is Room -> node.rectangle(p.width, p.depth)
            is Wall -> {
                node.addPoint(Vec3.ZERO)
                node.addPoint(Vec3.UNIT_X * p.width)
            }
        }
    }
}