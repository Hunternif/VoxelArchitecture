package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY

class StyleSize(
    var initial: Value<Number> = 0.vx,
    var min: Value<Number>? = null,
    var max: Value<Number>? = null,
) : StyleParameter {
    fun get(): Value<Number> = when {
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

fun Rule.height(block: StyleSize.() -> Value<Number>) {
    val value = StyleSize().apply { initial = block() }
    add(PropHeight, value.get().toDouble())
}

fun Rule.width(block: StyleSize.() -> Value<Number>) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get().toDouble())
}

fun Rule.depth(block: StyleSize.() -> Value<Number>) {
    val value = StyleSize().apply { initial = block() }
    add(PropDepth, value.get().toDouble())
}

/** Applies to both width and length. */
fun Rule.diameter(block: StyleSize.() -> Value<Number>) {
    val value = StyleSize().apply { initial = block() }
    add(PropWidth, value.get().toDouble())
    add(PropDepth, value.get().toDouble())
}

fun Rule.size(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    add(PropWidth, x.toDouble())
    add(PropHeight, y.toDouble())
    add(PropDepth, z.toDouble())
}
