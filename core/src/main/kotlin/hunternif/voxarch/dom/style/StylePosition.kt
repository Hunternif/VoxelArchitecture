package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomLineSegmentBuilder
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.util.round

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
        .round()
    node.origin.y += newValue
}

fun StyledNode<Node>.x(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val parent = node.parent
    val domParent = domBuilder.parent
    val style = StylePosition()
    val baseValue = when {
        domParent is DomLineSegmentBuilder -> domParent.end.length()
        parent is Room -> parent.width
        else -> 0.0
    }
    val newValue = style.block()
        .invoke(baseValue, seed + 10000012)
        .round()
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
        .round()
    node.origin.z += newValue
}

fun StyledNode<Node>.position(x: Dimension, y: Dimension, z: Dimension) {
    this.x { x }
    this.y { y }
    this.z { z }
}