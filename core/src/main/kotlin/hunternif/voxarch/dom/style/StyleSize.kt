package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StyleSize(
    var initial: Dimension = 0.vx,
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx,
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

val PropWidth = newNodeProperty<Node, Dimension> { value ->
    val node = domBuilder.node
    val baseValue = node.parent?.width ?: 0.0
    val newValue = value
        .invoke(baseValue, seed + 10000002)
        .round()
    node.width = newValue
}

val PropLength = newNodeProperty<Node, Dimension> { value ->
    val node = domBuilder.node
    val baseValue = node.parent?.length ?: 0.0
    val newValue = value
        .invoke(baseValue, seed + 10000003)
        .round()
    node.length = newValue
}

fun Rule.height2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value)
}

fun Rule.width2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value)
}

fun Rule.length2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropLength, value)
}

/** Applies to both width and length. */
fun Rule.diameter2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value)
    add(PropLength, value)
}

fun Rule.size2(x: Dimension, y: Dimension, z: Dimension) {
    add(PropWidth, StyleSize(x))
    add(PropHeight, StyleSize(y))
    add(PropLength, StyleSize(z))
}

@Deprecated("Use style2")
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
