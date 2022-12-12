package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.property.GlobalStyleOrderIndex
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/** Represents a DOM element for the purpose of styling. */
@CastleDsl
abstract class StyledElement(
    val parentNode: Node,
    internal open val domBuilder: DomBuilder,
    internal var seed: Long = domBuilder.seed,
    internal val styleClass: Set<String> = domBuilder.styleClass,
    internal val inheritedStyleClass: MutableSet<String> = linkedSetOf()
) {
    fun inherit(styleClasses: Iterable<String>): StyledElement {
        inheritedStyleClass.addAll(styleClasses)
        return this
    }
}

/** Represents a DOM element with a [Node] for the purpose of styling. */
@CastleDsl
class StyledNode<N : Node>(
    val node: N,
    parentNode: Node,
    override val domBuilder: DomNodeBuilder<N>,
) : StyledElement(parentNode, domBuilder)

/** Represents a DOM element with a [IGenerator] for the purpose of styling. */
@CastleDsl
class StyledGen<out G : IGenerator>(
    internal val gen: G,
    parentNode: Node,
    domBuilder: DomBuilder
) : StyledElement(parentNode, domBuilder)

/** Used as the base for Style DSL. */
@CastleDsl
interface StyleParameter

/** Container for all styles in a DOM. */
@CastleDsl
open class Stylesheet {
    val rules: ListMultimap<String, Rule> = ArrayListMultimap.create()

    /** Starting point to new style Rules. */
    fun add(block: RuleBuilder.() -> Unit): Stylesheet {
        RuleBuilder(this).apply(block)
        return this
    }

    fun addRule(rule: Rule) {
        if (rule.selector.styleClasses.isEmpty()) {
            rules.put(GLOBAL_STYLE, rule)
        } else {
            rule.selector.styleClasses.forEach { rules.put(it, rule) }
        }
    }

    open fun applyStyle(element: StyledElement) {
        val styleClasses = listOf(GLOBAL_STYLE) + element.styleClass
        styleClasses
            .flatMap { rules[it] }
            .toSet() // filter duplicates
            .filter { it.appliesTo(element) }
            .flatMap { it.declarations }
            .sortedBy { GlobalStyleOrderIndex[it.property] }
            .forEach { it.applyTo(element) }
    }

    fun clear() {
        rules.clear()
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"
    }
}
