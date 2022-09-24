package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.Visibility
import hunternif.voxarch.dom.builder.Visibility.*
import hunternif.voxarch.plan.Node

class StyleVisibility : StyleParameter

fun StyledNode<Node>.visibility(block: StyleVisibility.() -> Option<Visibility>) {
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

fun StyleVisibility.visible(): Option<Visibility> = option { _, _-> VISIBLE}
fun StyleVisibility.gone(): Option<Visibility> = option { _, _-> GONE}

fun StyledNode<Node>.visible() { domBuilder.visibility = VISIBLE }
fun StyledNode<Node>.gone() { domBuilder.visibility = GONE }