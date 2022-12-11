package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl

/**
 * The constructor arguments are the "CSS selector" for this rule.
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