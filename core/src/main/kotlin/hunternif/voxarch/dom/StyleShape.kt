package hunternif.voxarch.dom

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape

@CastleDsl
class StyleShape : StyleParameter

fun StyledNode<PolygonRoom>.shape(block: StyleShape.() -> Option<PolygonShape>) {
    val base = when (val parent = node.parent) {
        is PolygonRoom -> parent.shape
        else -> PolygonShape.SQUARE
    }
    node.shape = StyleShape().block().invoke(base, seed + 10000004)
}

var StyledNode<PolygonRoom>.shape: PolygonShape
    get() = node.shape
    set(value) { node.shape = value }

fun StyledNode<PolygonRoom>.edgeLength(block: StyleSize.() -> Dimension) {
    val baseValue = node.width
    val style = StyleSize(min = 1.vx)
    node.edgeLength = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000005)
}