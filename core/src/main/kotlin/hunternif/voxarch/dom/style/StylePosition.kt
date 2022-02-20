package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StylePosition : StyleParameter

// ================================ ORIGIN ================================

fun StyledNode<Node>.y(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.height
    val newValue = style.block()
        .invoke(baseValue, seed + 10000011)
        .round()
    node.origin.y += newValue
}

fun StyledNode<Node>.x(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.width
    val newValue = style.block()
        .invoke(baseValue, seed + 10000012)
        .round()
    node.origin.x += newValue
}

fun StyledNode<Node>.z(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.length
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


// ================================ START ================================

fun StyledNode<Room>.startY(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.height
    val newValue = style.block()
        .invoke(baseValue, seed + 10000011)
        .round()
    node.setCentered(false)
    node.start.y = newValue
}

fun StyledNode<Room>.startX(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.width
    val newValue = style.block()
        .invoke(baseValue, seed + 10000012)
        .round()
    node.setCentered(false)
    node.start.x = newValue
}

fun StyledNode<Room>.startZ(block: StylePosition.() -> Dimension) {
    val node = domBuilder.node
    val style = StylePosition()
    val baseValue = node.length
    val newValue = style.block()
        .invoke(baseValue, seed + 10000013)
        .round()
    node.setCentered(false)
    node.start.z = newValue
}

fun StyledNode<Room>.start(x: Dimension, y: Dimension, z: Dimension) {
    this.startX { x }
    this.startY { y }
    this.startZ { z }
}