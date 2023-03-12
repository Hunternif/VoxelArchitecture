package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.PolyRoom
import hunternif.voxarch.plan.PolyShape

class StyleShape : StyleParameter

val PropShape = newNodeProperty<PolyRoom, PolyShape>("shape", PolyShape.SQUARE) { value ->
    val baseValue = when (val parent = node.parent) {
        is PolyRoom -> parent.shape
        else -> PolyShape.SQUARE
    }
    node.shape = value.invoke(baseValue, seed + 10000004)
}

val PropEdgeLength = newNodeProperty<PolyRoom, Double>("edge-length", 1.0) { value ->
    val baseValue = node.width
    node.edgeLength = value.invoke(baseValue, seed + 10000005)
}

fun Rule.shape(block: StyleShape.() -> Value<PolyShape>) {
    add(PropShape, StyleShape().block())
}

fun Rule.edgeLength(block: StyleSize.() -> Value<Number>) {
    add(PropEdgeLength, StyleSize(min = 1.vx).block().toDouble())
}
