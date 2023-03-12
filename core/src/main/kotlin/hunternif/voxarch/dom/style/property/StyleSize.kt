package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.*
import hunternif.voxarch.plan.*
import hunternif.voxarch.util.Direction3D.*
import hunternif.voxarch.util.rotateY
import hunternif.voxarch.vector.Vec3

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

/** Shorthand for setting width + depth. */
val PropDiameter = newNodeProperty<Node, Double>("diameter", 0.0) { value ->
    PropWidth.applyTo(this, value)
    PropDepth.applyTo(this, value)
}

/**
 * Shorthand for setting width + height + depth.
 * Evaluates each axis separately, doesn't use the complete size vector as base.
 */
val PropSize = newNodeProperty<Node, Vec3>("size", Vec3(0, 0, 0)) { value ->
    PropWidth.applyTo(this, value.getX())
    PropDepth.applyTo(this, value.getZ())
    PropHeight.applyTo(this, value.getY())
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
    add(PropDiameter, value.get().toDouble())
}

fun Rule.size(block: () -> Value<Vec3>) {
    add(PropSize, block())
}

fun Rule.size(x: Value<Number>, y: Value<Number>, z: Value<Number>) {
    val value = value<Vec3>("$x $y $z", true) { base, seed ->
        Vec3(
            x(base.x, seed).toDouble(),
            y(base.y, seed).toDouble(),
            z(base.z, seed).toDouble(),
        )
    }
    add(PropSize, value)
}
