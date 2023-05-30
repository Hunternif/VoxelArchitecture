package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.StyleParameter
import hunternif.voxarch.dom.style.Value
import hunternif.voxarch.dom.style.newNodeProperty
import hunternif.voxarch.plan.ClipMask
import hunternif.voxarch.plan.Node

class StyleClipMask : StyleParameter

val PropClipMask = newNodeProperty<Node, ClipMask>("clip-mask", ClipMask.OFF) { value ->
    val baseValue = ClipMask.OFF
    val newValue = value.invoke(baseValue, seed + 10000041)
    node.clipMask = newValue
}

fun Rule.clipMask(block: StyleClipMask.() -> Value<ClipMask>) {
    add(PropClipMask, StyleClipMask().block())
}