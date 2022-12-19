package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.round

class StyleSize(
    var initial: Dimension = 0.vx,
    var min: Dimension? = null,
    var max: Dimension? = null,
) : StyleParameter {
    fun get(): Dimension = when {
        min != null && max != null -> initial.clamp(min!!, max!!)
        min != null -> max(min!!, initial)
        max != null -> min(max!!, initial)
        else -> initial
    }
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
    add(PropLength, x)
    add(PropHeight, y)
    add(PropWidth, z)
}

/** Inherit the value from the parent node. */
fun StyleSize.inherit(): Dimension = dimension("inherit") { base, _ -> base }