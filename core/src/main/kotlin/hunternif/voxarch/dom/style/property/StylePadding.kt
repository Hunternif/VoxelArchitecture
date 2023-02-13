package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.Dimension
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.StyleParameter
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.*
import kotlin.math.min

class StylePadding : StyleParameter

val PropPaddingTop = newNodeProperty<Node, Double>("padding top", 0.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value.invoke(baseValue, seed + 10000028)
    val currentSpace = parentNode.localMaxY - node.maxY
    val delta = min(newValue - currentSpace, node.naturalHeight)
    if (delta > 0) {
        node.naturalHeight -= delta
    }
}

val PropPaddingBottom = newNodeProperty<Node, Double>("padding bottom", 0.0) { value ->
    val baseValue = parentNode.naturalHeight
    val newValue = value.invoke(baseValue, seed + 10000029)
    val currentSpace = node.minY - parentNode.localMinY
    val delta = min(newValue - currentSpace, node.naturalHeight)
    if (delta > 0) {
        node.naturalHeight -= delta
        node.origin.y += delta
    }
}

val PropPaddingRightX = newNodeProperty<Node, Double>("padding right x", 0.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value.invoke(baseValue, seed + 10000030)
    val currentSpace = parentNode.localMaxX - node.maxX
    val delta = min(newValue - currentSpace, node.rotatedNaturalWidth)
    if (delta > 0) {
        node.rotatedWidth -= delta
    }
}

val PropPaddingLeftX = newNodeProperty<Node, Double>("padding left x", 0.0) { value ->
    val baseValue = parentNode.naturalWidth
    val newValue = value.invoke(baseValue, seed + 10000031)
    val currentSpace = node.minX - parentNode.localMinX
    val delta = min(newValue - currentSpace, node.rotatedNaturalWidth)
    if (delta > 0) {
        node.rotatedWidth -= delta
        node.origin.x += delta
    }
}

val PropPaddingFrontZ = newNodeProperty<Node, Double>("padding front z", 0.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value.invoke(baseValue, seed + 10000032)
    val currentSpace = parentNode.localMaxZ - node.maxZ
    val delta = min(newValue - currentSpace, node.rotatedNaturalDepth)
    if (delta > 0) {
        node.rotatedDepth -= delta
    }
}

val PropPaddingBackZ = newNodeProperty<Node, Double>("padding back z", 0.0) { value ->
    val baseValue = parentNode.naturalDepth
    val newValue = value.invoke(baseValue, seed + 10000033)
    val currentSpace = node.minZ - parentNode.localMinZ
    val delta = min(newValue - currentSpace, node.rotatedNaturalDepth)
    if (delta > 0) {
        node.rotatedDepth -= delta
        node.origin.z += delta
    }
}

fun Rule.paddingTop(block: StylePadding.() -> Dimension) {
    add(PropPaddingTop, StylePadding().block())
}

fun Rule.paddingBottom(block: StylePadding.() -> Dimension) {
    add(PropPaddingBottom, StylePadding().block())
}

fun Rule.paddingLeftX(block: StylePadding.() -> Dimension) {
    add(PropPaddingLeftX, StylePadding().block())
}

fun Rule.paddingRightX(block: StylePadding.() -> Dimension) {
    add(PropPaddingRightX, StylePadding().block())
}

fun Rule.paddingBackZ(block: StylePadding.() -> Dimension) {
    add(PropPaddingBackZ, StylePadding().block())
}

fun Rule.paddingFrontZ(block: StylePadding.() -> Dimension) {
    add(PropPaddingFrontZ, StylePadding().block())
}

fun Rule.paddingY(block: StylePadding.() -> Dimension) {
    val value = StylePadding().block()
    add(PropPaddingTop, value)
    add(PropPaddingBottom, value)
}

fun Rule.paddingX(block: StylePadding.() -> Dimension) {
    val value = StylePadding().block()
    add(PropPaddingLeftX, value)
    add(PropPaddingRightX, value)
}

fun Rule.paddingZ(block: StylePadding.() -> Dimension) {
    val value = StylePadding().block()
    add(PropPaddingBackZ, value)
    add(PropPaddingFrontZ, value)
}