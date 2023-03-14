package hunternif.voxarch.dom.style

/**
 * A list of CSS-like style declarations.
 * [selectors] define to which DOM elements this rule will apply.
 * The rule applies if the element matches *any* selectors in the set.
 */
class Rule(
    val selectors: LinkedHashSet<Selector> = linkedSetOf(),
) {
    constructor(selectors: Collection<Selector>)
        : this(LinkedHashSet(selectors))

    constructor(vararg selectors: Selector)
        : this(LinkedHashSet(selectors.toList()))

    val declarations = mutableListOf<Declaration<*>>()

    val propertyMap get() = PropertyMap(declarations)

    fun isEmpty(): Boolean = declarations.isEmpty()

    fun <T> add(prop: Property<T>, value: Value<T>) {
        add(Declaration(prop, value))
    }

    fun add(declaration: Declaration<*>) {
        declarations.add(declaration)
    }

    fun remove(declaration: Declaration<*>) {
        declarations.remove(declaration)
    }

    fun appliesTo(element: StyledElement<*>) =
        selectors.isEmpty() || selectors.any { it.appliesTo(element) }

    override fun toString(): String {
        val selStr = selectors.run { if (isEmpty()) "*" else joinToString(", ") }
        return "$selStr {\n${
            declarations.joinToString("\n") { "  $it" }
        }\n}"
    }
}