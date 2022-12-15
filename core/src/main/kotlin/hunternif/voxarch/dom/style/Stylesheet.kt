package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.style.property.GlobalStyleOrderIndex
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

@DslMarker
annotation class StyleDsl

/** Represents a DOM element for the purpose of styling. */
@StyleDsl
abstract class StyledElement(
    open val domBuilder: DomBuilder,
    val ctx: DomBuildContext,
    var seed: Long = ctx.seed + domBuilder.seedOffset
) {
    val parentNode: Node get() = ctx.parentNode
    val styleClass: Set<String> get() = domBuilder.styleClass
    val inheritedStyleClass: Set<String> get() = ctx.inheritedStyleClass
}

/** Represents a DOM element with a [Node] for the purpose of styling. */
@StyleDsl
class StyledNode<N : Node>(
    val node: N,
    override val domBuilder: DomNodeBuilder<N>,
    ctx: DomBuildContext,
) : StyledElement(domBuilder, ctx)

/** Represents a DOM element with a [IGenerator] for the purpose of styling. */
@StyleDsl
class StyledGen<out G : IGenerator>(
    internal val gen: G,
    domBuilder: DomBuilder,
    ctx: DomBuildContext,
) : StyledElement(domBuilder, ctx)

/** Used as the base for Style DSL. */
@StyleDsl
interface StyleParameter

/** Contains for styling DOM elements (nodes and generators). */
@StyleDsl
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
