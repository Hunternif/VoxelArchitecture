package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.Visibility
import hunternif.voxarch.dom.builder.Visibility.*
import hunternif.voxarch.dom.style.*

class StyleVisibility : StyleParameter

val PropVisibility = newDomProperty<Visibility>("visibility", VISIBLE) { value ->
    val baseValue = domBuilder.parent?.visibility ?: VISIBLE
    domBuilder.visibility = value.invoke(baseValue, seed + 10000010)
}

fun Rule.visibility(block: StyleVisibility.() -> Value<Visibility>) {
    add(PropVisibility, StyleVisibility().block())
}

fun Rule.visibleIf(predicate: () -> Boolean) {
    add(PropVisibility, set(if (predicate()) VISIBLE else GONE))
}

fun Rule.visible() {
    add(PropVisibility, set(VISIBLE))
}

fun Rule.gone() {
    add(PropVisibility, set(GONE))
}