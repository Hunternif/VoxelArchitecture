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
)

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

    /**
     * Register a style rule i.e. a list of style declarations.
     * They are not limited by Node type, they will apply wherever possible.
     * @param styleClass is the "CSS class".
     */
    fun style(
        styleClass: String,
        block: Rule.() -> Unit,
    ) {
        rules.put(styleClass, Rule(styleClass).apply(block))
    }

    /**
     * Register a style rule applied to specific DOM Builder instance.
     */
    fun styleFor(
        instance: DomBuilder,
        block: Rule.() -> Unit,
    ) {
        rules.put(GLOBAL_STYLE, Rule(destInstance = instance).apply(block))
    }

    /**
     * Register a style rule i.e. a list of style declarations.
     * These will be limited by type [T].
     * @param styleClass is the "CSS class".
     */
    inline fun <reified T> styleFor(
        styleClass: String = GLOBAL_STYLE,
        instance: DomBuilder? = null,
        noinline block: Rule.() -> Unit,
    ) {
        rules.put(styleClass, Rule(styleClass, T::class.java, instance).apply(block))
    }

    fun addRule(rule: Rule) {
        rules.put(rule.styleClass, rule)
    }

    open fun applyStyle(
        element: StyledElement,
        styleClass: Collection<String>
    ) {
        val styleClasses = listOf(GLOBAL_STYLE) + styleClass
        styleClasses
            .flatMap { rules[it] }
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

class CombinedStylesheet(private val stylesheets: Collection<Stylesheet>) : Stylesheet() {
    override fun applyStyle(
        element: StyledElement,
        styleClass: Collection<String>
    ) {
        stylesheets.forEach { it.applyStyle(element, styleClass) }
        super.applyStyle(element, styleClass)
    }
}

operator fun Stylesheet.plus(other: Stylesheet): Stylesheet =
    CombinedStylesheet(listOf(this, other))