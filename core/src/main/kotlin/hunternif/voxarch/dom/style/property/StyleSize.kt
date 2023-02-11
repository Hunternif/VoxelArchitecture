package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY

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
    val baseValue = parentNode.naturalHeight
    val newValue = value
        .invoke(baseValue, seed + 10000001)
    node.naturalHeight = newValue
}

val PropWidth = newNodeProperty<Node, Double>("width", 4.0) { value ->
    val rotatedDir = EAST.rotateY(node.rotationY)
    val baseValue = parentNode.localSizeInDir(rotatedDir).centricToNatural()
    val newValue = value
        .invoke(baseValue, seed + 10000002)
    node.naturalWidth = newValue
}

val PropDepth = newNodeProperty<Node, Double>("depth", 4.0) { value ->
    val rotatedDir = SOUTH.rotateY(node.rotationY)
    val baseValue = parentNode.localSizeInDir(rotatedDir).centricToNatural()
    val newValue = value
        .invoke(baseValue, seed + 10000003)
    node.naturalDepth = newValue
}

fun Rule.height(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value.get())
}

fun Rule.width(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
}

fun Rule.depth(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropDepth, value.get())
}

/** Applies to both width and length. */
fun Rule.diameter(block: StyleSize.() -> Dimension) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get())
    add(PropDepth, value.get())
}

fun Rule.size(x: Dimension, y: Dimension, z: Dimension) {
    add(PropWidth, x)
    add(PropHeight, y)
    add(PropDepth, z)
}

/** Inherit the value from the parent node. */
fun StyleSize.inherit(): Dimension = dimension("inherit", true) { base, _ -> base }