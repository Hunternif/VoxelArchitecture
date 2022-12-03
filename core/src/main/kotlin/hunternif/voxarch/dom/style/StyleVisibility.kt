package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.Visibility
import hunternif.voxarch.dom.builder.Visibility.*

class StyleVisibility : StyleParameter

val PropVisibility = newDomProperty<Value<Visibility>> { value ->
    val baseValue = domBuilder.parent.visibility
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