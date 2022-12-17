package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.Visibility
import hunternif.voxarch.dom.builder.Visibility.*
import hunternif.voxarch.dom.style.*

class StyleVisibility : StyleParameter

val PropVisibility = newDomProperty<DomBuilder, Visibility>("visibility", VISIBLE) { value ->
    val baseValue = ctx.parent.visibility
    domBuilder.visibility = value.invoke(baseValue, seed + 10000010)
}

fun Rule.visibility(block: StyleVisibility.() -> Value<Visibility>) {
    add(PropVisibility, StyleVisibility().block())
}

fun Rule.visibleIf(predicate: () -> Boolean) {
    add(PropVisibility, value("condition") { _, _ ->
        if (predicate()) VISIBLE else GONE
    })
}

fun Rule.visible() {
    add(PropVisibility, set(VISIBLE))
}

fun Rule.gone() {
    add(PropVisibility, set(GONE))
}