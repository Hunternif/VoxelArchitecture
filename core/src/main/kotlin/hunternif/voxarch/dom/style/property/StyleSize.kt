package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StyleSize(
    var initial: Dimension = 0.vx,
    var min: Dimension = 0.vx,
    var max: Dimension = Int.MAX_VALUE.vx,
) : StyleParameter {
    fun get(): Dimension = initial.clamp(min, max)
}

val PropHeight = newNodeProperty<Node, Double>("height", 4.0) { value ->
    val baseValue = parentNode.height
    val newValue = value
        .invoke(baseValue, seed + 10000001)
        .round()
    node.height = newValue
}

val PropWidth = newNodeProperty<Node, Double>("width", 4.0) { value ->
    val baseValue = parentNode.width
    val newValue = value
        .invoke(baseValue, seed + 10000002)
        .round()
    node.width = newValue
}

val PropLength = newNodeProperty<Node, Double>("length", 4.0) { value ->
    val baseValue = parentNode.length
    val newValue = value
        .invoke(baseValue, seed + 10000003)
        .round()
    node.length = newValue
}

fun Rule.height(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value.get())
}

fun Rule.width(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
}

fun Rule.length(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropLength, value.get())
}

/** Applies to both width and length. */
fun Rule.diameter(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
    add(PropLength, value.get())
}

fun Rule.size(x: Dimension, y: Dimension, z: Dimension) {
    add(PropWidth, x)
    add(PropHeight, y)
    add(PropLength, z)
}

/** Inherit the value from the parent node. */
fun StyleSize.inherit(): Dimension = dimension("inherit") { base, _ -> base }