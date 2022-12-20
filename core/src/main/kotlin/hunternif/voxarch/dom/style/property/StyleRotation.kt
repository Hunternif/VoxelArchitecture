package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.Node

class StyleRotation : StyleParameter

val PropRotation = newNodeProperty<Node, Double>("rotation", 0.0) { value ->
    val baseValue = parentNode.rotationY
    val newValue = value.invoke(baseValue, seed + 10000026)
    node.rotationY = newValue
}

fun Rule.rotation(block: StyleRotation.() -> Value<Double>) {
    add(PropRotation, StyleRotation().block())
}