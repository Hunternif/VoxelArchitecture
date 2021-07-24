package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall

class StylePosition : StyleParameter

fun StyledNode<Node>.y(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.height
        is Wall -> parent.height
        else -> 0.0
    }
    val newValue = style.block()
        .invoke(baseValue, seed + 10000011)
    node.origin.y += newValue
}

fun StyledNode<Node>.x(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.width
        else -> 0.0
    }
    val newValue = style.block()
        .invoke(baseValue, seed + 10000012)
    node.origin.x += newValue
}

fun StyledNode<Node>.z(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.length
        //TODO: when building on Wall, should I run X axis along the wall?
        is Wall -> parent.length
        else -> 0.0
    }
    val newValue = style.block()
        .invoke(baseValue, seed + 10000013)
    node.origin.z += newValue
}

fun StyledNode<Node>.position(x: Dimension, y: Dimension, z: Dimension) {
    this.x { x }
    this.y { y }
    this.z { z }
}