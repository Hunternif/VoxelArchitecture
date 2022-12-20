package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.util.ifNotEmpty

/**
 * CSS-like selector, defines to which DOM elements a rule will apply.
 *
 * @param styleClasses are the "CSS classes".
 * @param inheritedStyleClasses are the "CSS classes" inherited from parents.
 * @param types are the optional "CSS tags" (a Node or a DomBuilder).
 * @param instances are specific instances of DOM Builder.
 *      //TODO: replace instance with #id.
 */
class Selector {
    /** the "CSS classes" */
    val styleClasses: MutableSet<String> = linkedSetOf()

    /** the "CSS classes" inherited from parents */
    val inheritedStyleClasses: MutableSet<String> = linkedSetOf()

    /** "CSS tags" (a Node or a DomBuilder) */
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

    fun appliesTo(element: StyledElement<*>): Boolean {
        val type = when (element) {
            is StyledNode<*> -> element.node.javaClass
            else -> element.domBuilder.javaClass
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

    fun inherit(parentInstance: DomBuilder): Selector {
        inheritedStyleClasses.add(parentInstance.uniqueClass)
        return this
    }

    fun type(vararg type: Class<*>): Selector {
        types.addAll(type)
        return this
    }

    inline fun <reified T> type(): Selector {
        types.add(T::class.java)
        return this
    }

    fun instance(vararg instance: DomBuilder): Selector {
        instances.addAll(instance)
        return this
    }

    fun instances(instances: Iterable<DomBuilder>): Selector {
        this.instances.addAll(instances)
        return this
    }

    fun isEmpty(): Boolean =
        styleClasses.isEmpty() && types.isEmpty() && instances.isEmpty()
            && inheritedStyleClasses.isEmpty()

    override fun toString(): String {
        val inheritedStr = inheritedStyleClasses.joinToString(" ") { ".$it" }
        return mutableListOf<String>().apply {
            addAll(types.map { it.simpleName })
            addAll(styleClasses.map { ".$it" })
            inheritedStr.ifNotEmpty { add("[$it]") }
            addAll(instances.map { "#${it.javaClass.simpleName}" })
        }.joinToString(" ")
    }

    companion object {
        val EMPTY = Selector()
    }
}

/** Creates a selector with style names */
fun select(vararg styleClass: String) = Selector().apply {
    styleClasses.addAll(styleClass)
}

/** Creates a selector with inherited style names */
fun selectInherit(vararg styleClass: String) = Selector().apply {
    inheritedStyleClasses.addAll(styleClass)
}

/** Creates a selector for children of the given instance.
 * This includes non-immediate children too. */
fun selectInherit(parentInstance: DomBuilder) = Selector().apply {
    inheritedStyleClasses.add(parentInstance.uniqueClass)
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
    ret.inheritedStyleClasses.addAll(this.inheritedStyleClasses)
    ret.inheritedStyleClasses.addAll(other.inheritedStyleClasses)
    ret.types.addAll(this.types)
    ret.types.addAll(other.types)
    ret.instances.addAll(this.instances)
    ret.instances.addAll(other.instances)
    return ret
}