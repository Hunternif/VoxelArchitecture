package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3

class StylePosition : StyleParameter

// ========================== ABSOLUTE ORIGIN ============================

val PropY = newNodeProperty<Node, Double>("origin-y", 0.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value
        .invoke(baseValue, seed + 10000011)
    node.origin.y = newValue
}

val PropX = newNodeProperty<Node, Double>("origin-x", 0.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value
        .invoke(baseValue, seed + 10000012)
    node.origin.x = newValue
}

val PropZ = newNodeProperty<Node, Double>("origin-z", 0.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value
        .invoke(baseValue, seed + 10000013)
    node.origin.z = newValue
}

val PropPosition = newNodeProperty<Node, Vec3>("origin-vector", Vec3.ZERO) { value ->
    val baseValue = parentNode.naturalSize
    val newValue = value
        .invoke(baseValue, seed + 10000014)
    node.origin = newValue
}

fun Rule.y(block: StylePosition.() -> Value<Number>) {
    add(PropY, StylePosition().block().toDouble())
}

fun Rule.x(block: StylePosition.() -> Value<Number>) {
    add(PropX, StylePosition().block().toDouble())
}

fun Rule.z(block: StylePosition.() -> Value<Number>) {
    add(PropZ, StylePosition().block().toDouble())
}

fun Rule.position(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    add(PropX, x.toDouble())
    add(PropY, y.toDouble())
    add(PropZ, z.toDouble())
}

fun Rule.position(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropPosition, value("...", false, block))
}


// ============================= OFFSET ORIGIN =============================

val PropOffsetY = newNodeProperty<Node, Double>("offset-y", 0.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value
        .invoke(baseValue, seed + 10000015)
    node.origin.y += newValue
}

val PropOffsetX = newNodeProperty<Node, Double>("offset-x", 0.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value
        .invoke(baseValue, seed + 10000016)
    node.origin.x += newValue
}

val PropOffsetZ = newNodeProperty<Node, Double>("offset-z", 0.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value
        .invoke(baseValue, seed + 10000017)
    node.origin.z += newValue
}

val PropOffsetPosition = newNodeProperty<Node, Vec3>("offset-vector", Vec3.ZERO) { value ->
    val baseValue = parentNode.naturalSize
    val newValue = value
        .invoke(baseValue, seed + 10000018)
    node.origin += newValue
}

fun Rule.offsetY(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetY, StylePosition().block().toDouble())
}

fun Rule.offsetX(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetX, StylePosition().block().toDouble())
}

fun Rule.offsetZ(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetZ, StylePosition().block().toDouble())
}

fun Rule.offset(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    add(PropOffsetX, x.toDouble())
    add(PropOffsetY, y.toDouble())
    add(PropOffsetZ, z.toDouble())
}

fun Rule.offset(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropOffsetPosition, value("...", false, block))
}


// =========================== ABSOLUTE START ===========================

val PropStartY = newNodeProperty<Node, Double>("start-y", 0.0) { value ->
    val baseValue = node.naturalHeight
    val newValue = value
        .invoke(baseValue, seed + 10000021)
    node.start.y = newValue
}

val PropStartX = newNodeProperty<Node, Double>("start-x", 0.0) { value ->
    val baseValue = node.naturalWidth
    val newValue = value
        .invoke(baseValue, seed + 10000022)
    node.start.x = newValue
}

val PropStartZ = newNodeProperty<Node, Double>("start-z", 0.0) { value ->
    val baseValue = node.naturalDepth
    val newValue = value
        .invoke(baseValue, seed + 10000023)
    node.start.z = newValue
}

val PropStart = newNodeProperty<Node, Vec3>("start-vector", Vec3.ZERO) { value ->
    val baseValue = node.naturalSize
    val newValue = value
        .invoke(baseValue, seed + 10000024)
    node.start = newValue
}

fun Rule.startY(block: StylePosition.() -> Value<Number>) {
    add(PropStartY, StylePosition().block().toDouble())
}

fun Rule.startX(block: StylePosition.() -> Value<Number>) {
    add(PropStartX, StylePosition().block().toDouble())
}

fun Rule.startZ(block: StylePosition.() -> Value<Number>) {
    add(PropStartZ, StylePosition().block().toDouble())
}

fun Rule.start(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    add(PropStartX, x.toDouble())
    add(PropStartY, y.toDouble())
    add(PropStartZ, z.toDouble())
}

fun Rule.start(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropStart, value("...", false, block))
}



// ============================= OFFSET START =============================

val PropOffsetStartY = newNodeProperty<Node, Double>("offset-start-y", 0.0) { value ->
    val baseValue = node.naturalHeight
    val newValue = value
        .invoke(baseValue, seed + 10000028)
    node.start.y += newValue
}

val PropOffsetStartX = newNodeProperty<Node, Double>("offset-start-x", 0.0) { value ->
    val baseValue = node.naturalWidth
    val newValue = value
        .invoke(baseValue, seed + 10000029)
    node.start.x += newValue
}

val PropOffsetStartZ = newNodeProperty<Node, Double>("offset-start-z", 0.0) { value ->
    val baseValue = node.naturalDepth
    val newValue = value
        .invoke(baseValue, seed + 10000030)
    node.start.z += newValue
}

val PropOffsetStart = newNodeProperty<Node, Vec3>("offset-start-vector", Vec3.ZERO) { value ->
    val baseValue = node.naturalSize
    val newValue = value
        .invoke(baseValue, seed + 10000031)
    node.start += newValue
}

fun Rule.offsetStartY(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetStartY, StylePosition().block().toDouble())
}

fun Rule.offsetStartX(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetStartX, StylePosition().block().toDouble())
}

fun Rule.offsetStartZ(block: StylePosition.() -> Value<Number>) {
    add(PropOffsetStartZ, StylePosition().block().toDouble())
}

fun Rule.offsetStart(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    add(PropOffsetStartX, x.toDouble())
    add(PropOffsetStartY, y.toDouble())
    add(PropOffsetStartZ, z.toDouble())
}

fun Rule.offsetStart(block: (base: Vec3, seed: Long) -> Vec3) {
    add(PropOffsetStart, value("...", false, block))
}