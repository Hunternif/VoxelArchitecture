package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl

/**
 * A list of CSS-like style declarations.
 * [selector] defines to which DOM elements this rule will apply.
 */
@CastleDsl
class Rule(
    val selector: Selector = Selector(),
) {
    val declarations = mutableListOf<Declaration<*>>()
    fun <T> add(prop: Property<T>, value: Value<T>) {
        add(Declaration(prop, value))
    }

    fun add(declaration: Declaration<*>) {
        declarations.add(declaration)
    }

    fun remove(declaration: Declaration<*>) {
        declarations.remove(declaration)
    }

    fun appliesTo(element: StyledElement) = selector.appliesTo(element)
}