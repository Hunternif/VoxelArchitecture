package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StylePosition : StyleParameter

// ================================ ORIGIN ================================

val PropY = newNodeProperty<Node, Double>("offset y") { value ->
    val baseValue = node.height
    val newValue = value
        .invoke(baseValue, seed + 10000011)
        .round()
    node.origin.y += newValue
}

val PropX = newNodeProperty<Node, Double>("offset x") { value ->
    val baseValue = node.width
    val newValue = value
        .invoke(baseValue, seed + 10000012)
        .round()
    node.origin.x += newValue
}

val PropZ = newNodeProperty<Node, Double>("offset z") { value ->
    val baseValue = node.length
    val newValue = value
        .invoke(baseValue, seed + 10000013)
        .round()
    node.origin.z += newValue
}

fun Rule.y(block: StylePosition.() -> Dimension) {
    add(PropY, StylePosition().block())
}

fun Rule.x(block: StylePosition.() -> Dimension) {
    add(PropX, StylePosition().block())
}

fun Rule.z(block: StylePosition.() -> Dimension) {
    add(PropZ, StylePosition().block())
}

fun Rule.position(x: Dimension, y: Dimension, z: Dimension) {
    add(PropX, x)
    add(PropY, y)
    add(PropZ, z)
}


// ================================ START ================================

val PropStartY = newNodeProperty<Room, Double>("start y") { value ->
    val baseValue = node.height
    val newValue = value
        .invoke(baseValue, seed + 10000021)
        .round()
    node.setCentered(false)
    node.start.y = newValue
}

val PropStartX = newNodeProperty<Room, Double>("start x") { value ->
    val baseValue = node.width
    val newValue = value
        .invoke(baseValue, seed + 10000022)
        .round()
    node.setCentered(false)
    node.start.x = newValue
}

val PropStartZ = newNodeProperty<Room, Double>("start z") { value ->
    val baseValue = node.length
    val newValue = value
        .invoke(baseValue, seed + 10000023)
        .round()
    node.setCentered(false)
    node.start.z = newValue
}

fun Rule.startY(block: StylePosition.() -> Dimension) {
    add(PropStartY, StylePosition().block())
}

fun Rule.startX(block: StylePosition.() -> Dimension) {
    add(PropStartX, StylePosition().block())
}

fun Rule.startZ(block: StylePosition.() -> Dimension) {
    add(PropStartZ, StylePosition().block())
}

fun Rule.start(x: Dimension, y: Dimension, z: Dimension) {
    add(PropStartX, x)
    add(PropStartY, y)
    add(PropStartZ, z)
}