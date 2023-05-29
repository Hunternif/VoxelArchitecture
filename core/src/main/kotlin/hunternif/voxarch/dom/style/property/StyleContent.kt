package hunternif.voxarch.dom.style.property

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.Rule
import hunternif.voxarch.dom.style.newDomProperty
import hunternif.voxarch.dom.style.value

/**
 * Executes dom builder at this element.
 * This property's value is a DomBuilder that's added as a new child.
 */
val PropContent = newDomProperty<DomBuilder, DomBuilder?>("content", null) { value ->
    // this class name will be the same for each invocation of this exact 'content' value:
    val invocationClass = "_content_${value.hashCode()}"
    val recursions = ctx.lineage.count { invocationClass in it.styleClass }
    if (recursions >= maxContentRecursions) {
        return@newDomProperty
    }

    val baseValue = this.domBuilder // this value is generally not used
    val newContent = value.invoke(baseValue, seed + 10000039) ?: return@newDomProperty

    newContent.addStyle(invocationClass)
    domBuilder.addChild(newContent)
    ctx.stats.addedContent.add(domBuilder to newContent)
}

private const val maxContentRecursions = 2

/** Executes dom builder at this element */
fun Rule.content(block: DomBuilder.() -> Unit) {
    add(PropContent, value { _, _ -> DomBuilder().apply(block) })
}