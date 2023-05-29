package hunternif.voxarch.dom.style.property

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.StyleParameter
import hunternif.voxarch.dom.style.Value
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.Node

class StyleBuilder : StyleParameter

/**
 * Sets Node's custom builder
 */
val PropBuilder = newNodeProperty<Node, Builder<*>?>("builder", null) { value ->
    val baseValue = node.builder
    val newValue = value.invoke(baseValue, seed + 10000040)
    node.builder = newValue
}

fun Rule.builder(block: StyleBuilder.() -> Value<Builder<*>?>) {
    add(PropBuilder, StyleBuilder().block())
}