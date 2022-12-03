package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.PolygonRoom
import hunternif.voxarch.plan.PolygonShape

class StyleShape : StyleParameter

val PropShape = newNodeProperty<PolygonRoom, Value<PolygonShape>>("shape") { value ->
    val baseValue = when (val parent = node.parent) {
        is PolygonRoom -> parent.shape
        else -> PolygonShape.SQUARE
    }
    node.shape = value.invoke(baseValue, seed + 10000004)
}

val PropEdgeLength = newNodeProperty<PolygonRoom, Dimension>("edge length") { value ->
    val baseValue = node.width
    node.edgeLength = value.invoke(baseValue, seed + 10000005)
}

fun Rule.shape(block: StyleShape.() -> Value<PolygonShape>) {
    add(PropShape, StyleShape().block())
}

fun Rule.edgeLength(block: StyleSize.() -> Dimension) {
    add(PropEdgeLength, StyleSize(min = 1.vx).block())
}
