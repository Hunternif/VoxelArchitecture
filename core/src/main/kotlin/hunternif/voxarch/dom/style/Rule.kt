package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder

/**
 * The constructor arguments are the "CSS selector" for this rule.
 * @param styleClass is the "CSS class".
 * @param destType is the optional "CSS tag" (a node or a generator).
 * @param destInstance is a specific instance of DOM Builder.
 *      //TODO: replace instance with #id.
 */
@CastleDsl
class Rule(
    var styleClass: String = Stylesheet.GLOBAL_STYLE,
    var destType: Class<*>? = null,
    var destInstance: DomBuilder? = null,
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

    fun appliesToType(type: Class<*>) =
        destType?.isAssignableFrom(type) ?: true

    fun appliesToInstance(instance: DomBuilder) =
        destInstance?.let { it == instance } ?: true

    fun appliesTo(element: StyledElement): Boolean {
        val type = when (element) {
            is StyledNode<*> -> element.node.javaClass
            is StyledGen<*> -> element.gen.javaClass
            else -> return false
        }
        return appliesToInstance(element.domBuilder) &&
            appliesToType(type)
    }
}