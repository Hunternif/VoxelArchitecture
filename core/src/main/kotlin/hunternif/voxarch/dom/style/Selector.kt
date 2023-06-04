package hunternif.voxarch.dom.style

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.util.ifNotEmpty

/**
 * CSS-like selector, defines to which DOM elements a rule will apply.
 */
class Selector {
    /**
     * "CSS classes"
     * The rule applies if the element uses *all* these classes.
     */
    val styleClasses: MutableSet<String> = linkedSetOf()

    /**
     * Matches any ancestor in the lineage (build chain).
     * The rule applies if any of the element's ancestors match *any* selector
     * in this set.
     */
    val ancestorSelectors: MutableSet<Selector> = linkedSetOf()

    /**
     * Matches only the direct parent.
     * The rule applies if the element's direct parent matches *any* selector
     * in this set.
     */
    val parentSelectors: MutableSet<Selector> = linkedSetOf()

    /**
     * "CSS tags" (a Node or a DomBuilder).
     * The rule applies if the element's type matches *any* type in this set.
     */
    val types: MutableSet<Class<*>> = linkedSetOf()

    /**
     * If true, this selector will not apply to any element.
     * This is used to indicate invalid rules without breaking parsing of other rules.
     */
    var isInvalid: Boolean = false

    /** specific instances of DOM Builder */
    //TODO: replace instance with #id.
    val instances: MutableSet<DomBuilder> = linkedSetOf()

    private fun appliesToStyleClass(styleClasses: Set<String>) =
        this.styleClasses.isEmpty() || styleClasses.containsAll(this.styleClasses)

    private fun appliesToType(type: Class<*>) =
        types.isEmpty() || types.any { it.isAssignableFrom(type) }

    private fun appliesToInstance(instance: DomBuilder) =
        instances.isEmpty() || instances.any { it == instance }

    private fun appliesToParent(parent: StyledElement<*>?): Boolean {
        if (parentSelectors.isEmpty()) return true
        if (parent == null) return false
        return parentSelectors.any { it.appliesTo(parent) }
    }

    private fun appliesToAncestor(lineage: List<StyledElement<*>>): Boolean {
        if (ancestorSelectors.isEmpty()) return true
        return lineage.any { parent ->
            ancestorSelectors.any { it.appliesTo(parent) }
        }
    }

    fun appliesTo(element: StyledElement<*>): Boolean {
        if (isInvalid) return false
        val type = when (element) {
            is StyledNode<*> -> element.node.javaClass
            else -> element.domBuilder.javaClass
        }
        return appliesToInstance(element.domBuilder) &&
            appliesToType(type) &&
            appliesToStyleClass(element.styleClass) &&
            appliesToParent(element.parent) &&
            appliesToAncestor(element.ctx.lineage)
    }

    fun style(vararg styleClass: String): Selector {
        styleClasses.addAll(styleClass)
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
            && parentSelectors.isEmpty() && ancestorSelectors.isEmpty()

    override fun toString(): String {
        val parentStr = parentSelectors.joinToString(", ")
        val ancestorStr = ancestorSelectors.joinToString(", ")
        return mutableListOf<String>().apply {
            ancestorStr.ifNotEmpty { add("[$it]") }
            parentStr.ifNotEmpty { add("$it >") }
            addAll(types.map { it.simpleName })
            addAll(styleClasses.map { ".$it" })
            addAll(instances.map { "#${it.javaClass.simpleName}" })
        }
            .joinToString(" ")
            .ifEmpty { "*" }
    }

    companion object {
        val EMPTY = Selector()
    }
}

/** Creates a selector with style names */
fun select(vararg styleClass: String) = Selector().apply {
    styleClasses.addAll(styleClass)
}

/** Creates a selector for immediate children of elements with style names.
 * This includes only immediate children. */
fun selectChildOf(vararg styleClass: String) = Selector().apply {
    parentSelectors.add(select(*styleClass))
}

/** Creates a selector for direct children of the given instance.
 * This includes only immediate children. */
fun selectChildOf(parentInstance: DomBuilder) = Selector().apply {
    parentSelectors.add(select(parentInstance))
}

/** Creates a selector for descendants of elements with style name.
 * This includes non-immediate children too. */
fun selectDescendantOf(vararg styleClass: String) = Selector().apply {
    ancestorSelectors.add(select(*styleClass))
}

/** Creates a selector for descendants of the given instance.
 * This includes non-immediate children too. */
fun selectDescendantOf(parentInstance: DomBuilder) = Selector().apply {
    ancestorSelectors.add(select(parentInstance))
}

/** Creates a selector with types */
fun select(vararg type: Class<*>) = Selector().apply {
    types.addAll(type)
}

/** Creates a selector with instances */
fun select(vararg instance: DomBuilder) = Selector().apply {
    instances.addAll(instance)
}

/**
 * Combines this selector with [other], so that they both need to match.
 * This is actually an AND operator.
 */
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
    ret.parentSelectors.addAll(this.parentSelectors)
    ret.parentSelectors.addAll(other.parentSelectors)
    ret.ancestorSelectors.addAll(this.ancestorSelectors)
    ret.ancestorSelectors.addAll(other.ancestorSelectors)
    return ret
}