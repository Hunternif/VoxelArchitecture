package hunternif.voxarch.dom.builder

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.rectangle
import hunternif.voxarch.vector.Vec3

class DomPathBuilder : DomNodeBuilder<Path>(Path::class.java, { Path() }) {
    override fun buildNode(ctx: DomBuildContext, node: Path) {
        // match the shape of parent
        val p = ctx.parentNode
        when {
            ctx.parent is DomLineSegmentBuilder -> {
                // this builder rotates its children,
                // so the path lies along the X axis
                node.addPoint(Vec3.ZERO)
                node.addPoint(Vec3.UNIT_X * ctx.parent.length)
            }
            p is PolyRoom -> node.addPoints(p.polygon.points)
            p is Room -> node.rectangle(p.length, p.width)
            p is Wall -> {
                node.addPoint(Vec3.ZERO)
                node.addPoint(p.innerEnd)
            }
        }
    }
}