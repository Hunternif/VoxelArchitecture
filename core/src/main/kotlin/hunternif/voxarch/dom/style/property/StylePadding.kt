package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.*
import kotlin.math.min

class StylePadding : StyleParameter

// Padding is the distance from this node to its parent's boundary.
// Setting padding on a node shrinks it and moves it.

val PropPaddingTop = newNodeProperty<Node, Double>("padding-top", 1.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value.invoke(baseValue, seed + 10000028)
    val currentSpace = parentNode.localMaxY - node.maxY
    val delta = min(newValue - currentSpace, node.naturalHeight)
    if (delta > 0) {
        node.naturalHeight -= delta
    }
}

val PropPaddingBottom = newNodeProperty<Node, Double>("padding-bottom", 1.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value.invoke(baseValue, seed + 10000029)
    val currentSpace = node.minY - parentNode.localMinY
    val delta = min(newValue - currentSpace, node.naturalHeight)
    if (delta > 0) {
        node.naturalHeight -= delta
        node.origin.y += delta
    }
}

val PropPaddingRightX = newNodeProperty<Node, Double>("padding-right-x", 1.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value.invoke(baseValue, seed + 10000030)
    val currentSpace = parentNode.localMaxX - node.maxX
    val delta = min(newValue - currentSpace, node.rotatedNaturalWidth)
    if (delta > 0) {
        node.rotatedWidth -= delta
        node.maxX = parentNode.localMaxX - newValue
    }
}

val PropPaddingLeftX = newNodeProperty<Node, Double>("padding-left-x", 1.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value.invoke(baseValue, seed + 10000031)
    val currentSpace = node.minX - parentNode.localMinX
    val delta = min(newValue - currentSpace, node.rotatedNaturalWidth)
    if (delta > 0) {
        node.rotatedWidth -= delta
        node.minX = parentNode.localMinX + newValue
    }
}

val PropPaddingFrontZ = newNodeProperty<Node, Double>("padding-front-z", 1.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value.invoke(baseValue, seed + 10000032)
    val currentSpace = parentNode.localMaxZ - node.maxZ
    val delta = min(newValue - currentSpace, node.rotatedNaturalDepth)
    if (delta > 0) {
        node.rotatedDepth -= delta
        node.maxZ = parentNode.localMaxZ - newValue
    }
}

val PropPaddingBackZ = newNodeProperty<Node, Double>("padding-back-z", 1.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value.invoke(baseValue, seed + 10000033)
    val currentSpace = node.minZ - parentNode.localMinZ
    val delta = min(newValue - currentSpace, node.rotatedNaturalDepth)
    if (delta > 0) {
        node.rotatedDepth -= delta
        node.minZ = parentNode.localMinZ + newValue
    }
}

val PropPaddingX = newNodeProperty<Node, Double>("padding-x", 1.0) { value ->
    PropPaddingLeftX.applyTo(this, value)
    PropPaddingRightX.applyTo(this, value)
}

val PropPaddingY = newNodeProperty<Node, Double>("padding-y", 1.0) { value ->
    PropPaddingTop.applyTo(this, value)
    PropPaddingBottom.applyTo(this, value)
}

val PropPaddingZ = newNodeProperty<Node, Double>("padding-z", 1.0) { value ->
    PropPaddingBackZ.applyTo(this, value)
    PropPaddingFrontZ.applyTo(this, value)
}

fun Rule.paddingTop(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingTop, StylePadding().block().toDouble())
}

fun Rule.paddingBottom(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingBottom, StylePadding().block().toDouble())
}

fun Rule.paddingLeftX(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingLeftX, StylePadding().block().toDouble())
}

fun Rule.paddingRightX(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingRightX, StylePadding().block().toDouble())
}

fun Rule.paddingBackZ(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingBackZ, StylePadding().block().toDouble())
}

fun Rule.paddingFrontZ(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingFrontZ, StylePadding().block().toDouble())
}

fun Rule.paddingY(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingY, StylePadding().block().toDouble())
}

fun Rule.paddingX(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingX, StylePadding().block().toDouble())
}

fun Rule.paddingZ(block: StylePadding.() -> Value<Number>) {
    add(PropPaddingZ, StylePadding().block().toDouble())
}