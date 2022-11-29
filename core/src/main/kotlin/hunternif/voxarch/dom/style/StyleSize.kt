package hunternif.voxarch.dom.style

import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StyleSize(
    var initial: Dimension = 0.vx,
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx,
) : StyleParameter {
    fun get(): Dimension = initial.clamp(min, max)
}

val PropHeight = newNodeProperty<Node, Dimension> { value ->
    val baseValue = parent.height
    val newValue = value
        .invoke(baseValue, seed + 10000001)
        .round()
    node.height = newValue
}

val PropWidth = newNodeProperty<Node, Dimension> { value ->
    val baseValue = parent.width
    val newValue = value
        .invoke(baseValue, seed + 10000002)
        .round()
    node.width = newValue
}

val PropLength = newNodeProperty<Node, Dimension> { value ->
    val baseValue = parent.length
    val newValue = value
        .invoke(baseValue, seed + 10000003)
        .round()
    node.length = newValue
}

fun Rule.height2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value.get())
}

fun Rule.width2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
}

fun Rule.length2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropLength, value.get())
}

/** Applies to both width and length. */
fun Rule.diameter2(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
    add(PropLength, value.get())
}

fun Rule.size2(x: Dimension, y: Dimension, z: Dimension) {
    add(PropWidth, x)
    add(PropHeight, y)
    add(PropLength, z)
}

