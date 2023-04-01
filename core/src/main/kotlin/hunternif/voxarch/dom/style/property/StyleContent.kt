package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.dom.style.value

/** Executes dom builder at this element */
val PropContent = newDomProperty<DomBuilder, DomBuilder?>("content", null) { value ->
    val baseValue = this.domBuilder
    value.invoke(baseValue, seed)
}

/** Executes dom builder at this element */
fun Rule.content(block: DomBuilder.() -> Unit) {
    add(PropContent, value { base, _ -> base?.apply(block) })
}