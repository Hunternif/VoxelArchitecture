package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.Room
import hunternif.voxarch.plan.Wall
import hunternif.voxarch.util.round

class StyleSize(
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx
) : StyleParameter

fun StyledNode<Node>.height(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.height
        is Wall -> parent.height
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000001)
        .round()
    when (node) {
        is Room -> node.height = newValue
        is Wall -> node.height = newValue
    }
}

fun StyledNode<Node>.width(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.width
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000002)
        .round()
    when (node) {
        is Room -> node.width = newValue
    }
}

fun StyledNode<Node>.length(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = when(val parent = node.parent) {
        is Room -> parent.length
        is Wall -> parent.length
        else -> 0.0
    }
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000003)
        .round()
    when (node) {
        is Room -> node.length = newValue
    }
}

/** Applies to both width and length. */
fun StyledNode<Node>.diameter(block: StyleSize.() -> Dimension) {
    length(block)
    width(block)
}

fun StyledNode<Node>.size(x: Dimension, y: Dimension, z: Dimension) {
    width { x }
    height { y }
    length { z }
}