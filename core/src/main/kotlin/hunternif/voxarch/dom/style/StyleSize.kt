package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StyleSize(
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx,
    var initial: Dimension = 0.vx,
) : StyleParameter, IStyleValue<Dimension> {
    override fun get(): Dimension = initial.clamp(min, max)
}

val PropHeight = newNodeProperty<Node, Dimension> { value ->
    val node = domBuilder.node
    val baseValue = node.parent?.height ?: 0.0
    val newValue = value
        .invoke(baseValue, seed + 10000001)
        .round()
    node.height = newValue
}

fun Rule.height2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value)
}

fun StyledNode<Node>.height(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = node.parent?.height ?: 0.0
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000001)
        .round()
    node.height = newValue
}

fun StyledNode<Node>.width(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = node.parent?.width ?: 0.0
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000002)
        .round()
    node.width = newValue
}

fun StyledNode<Node>.length(block: StyleSize.() -> Dimension) {
    val node = domBuilder.node
    val style = StyleSize()
    val baseValue = node.parent?.length ?: 0.0
    val newValue = style.block()
        .clamp(style.min, style.max)
        .invoke(baseValue, seed + 10000003)
        .round()
    node.length = newValue
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