package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.Visibility
import hunternif.voxarch.dom.builder.Visibility.*
import hunternif.voxarch.plan.Node

class StyleVisibility : StyleParameter

fun StyledNode<Node>.visibility(block: StyleVisibility.() -> Value<Visibility>) {
    val base = domBuilder.parent.visibility
    domBuilder.visibility = StyleVisibility().block()
        .invoke(base, seed + 10000010)
}

var StyledNode<Node>.visibility: Visibility
    get() = domBuilder.visibility
    set(value) { domBuilder.visibility = value }

fun StyledNode<Node>.visibleIf(predicate: () -> Boolean) {
    domBuilder.visibility = if (predicate()) VISIBLE else GONE
}

fun StyleVisibility.visible(): Value<Visibility> = value { _, _-> VISIBLE}
fun StyleVisibility.gone(): Value<Visibility> = value { _, _-> GONE}

fun StyledNode<Node>.visible() { domBuilder.visibility = VISIBLE }
fun StyledNode<Node>.gone() { domBuilder.visibility = GONE }