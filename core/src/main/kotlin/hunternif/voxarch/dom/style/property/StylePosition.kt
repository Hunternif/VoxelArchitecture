package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3

class StylePosition : StyleParameter

// ========================== ABSOLUTE ORIGIN ============================

val PropY = newNodeProperty<Node, Double>("origin y", 0.0) { value ->
    val baseValue = node.height
    val newValue = value
        .invoke(baseValue, seed + 10000011)
    node.origin.y = newValue
}

val PropX = newNodeProperty<Node, Double>("origin x", 0.0) { value ->
    val baseValue = node.width
    val newValue = value
        .invoke(baseValue, seed + 10000012)
    node.origin.x = newValue
}

val PropZ = newNodeProperty<Node, Double>("origin z", 0.0) { value ->
    val baseValue = node.length
    val newValue = value
        .invoke(baseValue, seed + 10000013)
    node.origin.z = newValue
}

val PropPosition = newNodeProperty<Node, Vec3>("origin vector", Vec3.ZERO) { value ->
    val baseValue = node.origin
    val newValue = value
        .invoke(baseValue, seed + 10000014)
    node.origin = newValue
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

fun Rule.position(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropPosition, value("...", block))
}


// ============================= OFFSET ORIGIN =============================

val PropOffsetY = newNodeProperty<Node, Double>("offset y", 0.0) { value ->
    val baseValue = node.height
    val newValue = value
        .invoke(baseValue, seed + 10000015)
    node.origin.y += newValue
}

val PropOffsetX = newNodeProperty<Node, Double>("offset x", 0.0) { value ->
    val baseValue = node.width
    val newValue = value
        .invoke(baseValue, seed + 10000016)
    node.origin.x += newValue
}

val PropOffsetZ = newNodeProperty<Node, Double>("offset z", 0.0) { value ->
    val baseValue = node.length
    val newValue = value
        .invoke(baseValue, seed + 10000017)
    node.origin.z += newValue
}

val PropOffsetPosition = newNodeProperty<Node, Vec3>("offset vector", Vec3.ZERO) { value ->
    val baseValue = node.origin
    val newValue = value
        .invoke(baseValue, seed + 10000018)
    node.origin += newValue
}

fun Rule.offsetY(block: StylePosition.() -> Dimension) {
    add(PropOffsetY, StylePosition().block())
}

fun Rule.offsetX(block: StylePosition.() -> Dimension) {
    add(PropOffsetX, StylePosition().block())
}

fun Rule.offsetZ(block: StylePosition.() -> Dimension) {
    add(PropOffsetZ, StylePosition().block())
}

fun Rule.offset(x: Dimension, y: Dimension, z: Dimension) {
    add(PropOffsetX, x)
    add(PropOffsetY, y)
    add(PropOffsetZ, z)
}

fun Rule.offset(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropOffsetPosition, value("...", block))
}


// =========================== ABSOLUTE START ===========================

val PropStartY = newNodeProperty<Room, Double>("start y", 0.0) { value ->
    val baseValue = node.height
    val newValue = value
        .invoke(baseValue, seed + 10000021)
    node.setCentered(false)
    node.start.y = newValue
}

val PropStartX = newNodeProperty<Room, Double>("start x", 0.0) { value ->
    val baseValue = node.width
    val newValue = value
        .invoke(baseValue, seed + 10000022)
    node.setCentered(false)
    node.start.x = newValue
}

val PropStartZ = newNodeProperty<Room, Double>("start z", 0.0) { value ->
    val baseValue = node.length
    val newValue = value
        .invoke(baseValue, seed + 10000023)
    node.setCentered(false)
    node.start.z = newValue
}

val PropStart = newNodeProperty<Room, Vec3>("start vector", Vec3.ZERO) { value ->
    val baseValue = node.start
    val newValue = value
        .invoke(baseValue, seed + 10000024)
    node.start = newValue
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

fun Rule.start(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropStart, value("...", block))
}