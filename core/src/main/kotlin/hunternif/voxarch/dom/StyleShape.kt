package hunternif.voxarch.dom

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape

@CastleDsl
class StyleShape

fun StyledNode<PolygonRoom>.shape(block: StyleShape.() -> Option<PolygonShape>) {
    node.shape = StyleShape().block().invoke(seed + 10000004)
}

var StyledNode<PolygonRoom>.shape: PolygonShape
    get() = node.shape
    set(value) { node.shape = value }

fun StyledNode<PolygonRoom>.edgeLength(block: StyleSize.() -> Dimension) {
    val style = StyleSize(min = 1.vx)
    node.edgeLength = style.block()
        .clamp(style.min, style.max)
        .invoke(node.width, seed + 10000005)
}