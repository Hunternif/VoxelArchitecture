package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder

/**
 * CSS-like selector, defines to which DOM elements a rule will apply.
 *
 * @param styleClasses are the "CSS classes".
 * @param inheritedStyleClasses are the "CSS classes" inherited from parents.
 * @param types are the optional "CSS tags" (a node or a generator).
 * @param instances are specific instances of DOM Builder.
 *      //TODO: replace instance with #id.
 */
class Selector {
    /** the "CSS classes" */
    val styleClasses: MutableSet<String> = linkedSetOf()

    /** the "CSS classes" inherited from parents */
    val inheritedStyleClasses: MutableSet<String> = linkedSetOf()

    /** "CSS tags" (a node or a generator) */
    val types: MutableSet<Class<*>> = linkedSetOf()

    /** specific instances of DOM Builder */
    val instances: MutableSet<DomBuilder> = linkedSetOf()

    fun appliesToStyleClass(styleClasses: Set<String>) =
        this.styleClasses.isEmpty() || styleClasses.containsAll(this.styleClasses)

    fun appliesToInheritedStyleClass(styleClasses: Set<String>) =
        inheritedStyleClasses.isEmpty() ||
            styleClasses.containsAll(inheritedStyleClasses)

    fun appliesToType(type: Class<*>) =
        types.isEmpty() || types.any { it.isAssignableFrom(type) }

    fun appliesToInstance(instance: DomBuilder) =
        instances.isEmpty() || instances.any { it == instance }

    fun appliesTo(element: StyledElement): Boolean {
        val type = when (element) {
            is StyledNode<*> -> element.node.javaClass
            is StyledGen<*> -> element.gen.javaClass
            else -> return false
        }
        return appliesToInstance(element.domBuilder) &&
            appliesToType(type) &&
            appliesToStyleClass(element.styleClass) &&
            appliesToInheritedStyleClass(
                element.inheritedStyleClass + element.styleClass)
    }

    fun style(vararg styleClass: String): Selector {
        styleClasses.addAll(styleClass)
        return this
    }

    fun inheritStyle(vararg styleClass: String): Selector {
        inheritedStyleClasses.addAll(styleClass)
        return this
    }

    fun type(vararg type: Class<*>): Selector {
        types.addAll(type)
        return this
    }

    fun instance(vararg instance: DomBuilder): Selector {
        instances.addAll(instance)
        return this
    }

    fun isEmpty(): Boolean =
        styleClasses.isEmpty() && types.isEmpty() && instances.isEmpty()
            && inheritedStyleClasses.isEmpty()

    companion object {
        val EMPTY = Selector()
    }
}

/** Creates a selector with style names */
fun select(vararg styleClass: String) = Selector().apply {
    styleClasses.addAll(styleClass)
}

/** Creates a selector with inhertied style names */
fun selectInherit(vararg styleClass: String) = Selector().apply {
    inheritedStyleClasses.addAll(styleClass)
}

/** Creates a selector with types */
fun select(vararg type: Class<*>) = Selector().apply {
    types.addAll(type)
}

/** Creates a selector with instances */
fun select(vararg instance: DomBuilder) = Selector().apply {
    instances.addAll(instance)
}

operator fun Selector.plus(other: Selector): Selector {
    if (this.isEmpty()) return other
    else if (other.isEmpty()) return this
    val ret = Selector()
    ret.styleClasses.addAll(this.styleClasses)
    ret.styleClasses.addAll(other.styleClasses)
    ret.types.addAll(this.types)
    ret.types.addAll(other.types)
    ret.instances.addAll(this.instances)
    ret.instances.addAll(other.instances)
    return ret
}