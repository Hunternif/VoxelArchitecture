package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.dom.builder.findParentNode
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/** Represents a DOM element for the purpose of styling. */
@CastleDsl
abstract class StyledElement(
    internal open val domBuilder: DomBuilder,
    internal var seed: Long = domBuilder.seed,
)

/** Represents a DOM element with a [Node] for the purpose of styling. */
@CastleDsl
class StyledNode<out N : Node>(
    override val domBuilder: DomNodeBuilder<N>,
) : StyledElement(domBuilder) {
    val node: N get() = domBuilder.node
    val parent: Node get() = domBuilder.findParentNode()
}

/** Represents a DOM element with a [IGenerator] for the purpose of styling. */
@CastleDsl
class StyledGen<out G : IGenerator>(
    internal val gen: G,
    domBuilder: DomBuilder
) : StyledElement(domBuilder)

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
     * Register a style rule i.e. a list of style declarations.
     * These will be limited by type [T].
     * @param styleClass is the "CSS class".
     */
    inline fun <reified T> styleFor(
        styleClass: String = GLOBAL_STYLE,
        noinline block: Rule.() -> Unit,
    ) {
        rules.put(styleClass, Rule(styleClass, T::class.java).apply(block))
    }

    fun addRule(rule: Rule) {
        rules.put(rule.styleClass, rule)
    }

    internal open fun apply(
        domBuilder: DomBuilder,
        styleClass: Collection<String>
    ) {
        val styleClasses = listOf(GLOBAL_STYLE) + styleClass
        // apply generator styles
        domBuilder.generators.forEach { gen ->
            val styledGen = StyledGen(gen, domBuilder)
            val genClass = gen.javaClass
            styleClasses
                .flatMap { rules[it] }
                .filter { it.destType?.isAssignableFrom(genClass) ?: true }
                .flatMap { it.declarations }
                .sortedBy { GlobalStyleOrderIndex[it.property] }
                .forEach { it.applyTo(styledGen) }
        }
        // apply node styles
        if (domBuilder is DomNodeBuilder<*>) {
            val styledNode = StyledNode(domBuilder)
            val nodeClass = domBuilder.node.javaClass
            styleClasses
                .flatMap { rules[it] }
                .filter { it.destType?.isAssignableFrom(nodeClass) ?: true }
                .flatMap { it.declarations }
                .sortedBy { GlobalStyleOrderIndex[it.property] }
                .forEach { it.applyTo(styledNode) }
        }
    }

    fun clear() {
        rules.clear()
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"
    }
}

class CombinedStylesheet(private val stylesheets: Collection<Stylesheet>) : Stylesheet() {
    override fun apply(
        domBuilder: DomBuilder,
        styleClass: Collection<String>
    ) {
        stylesheets.forEach { it.apply(domBuilder, styleClass) }
    }
}

operator fun Stylesheet.plus(other: Stylesheet): Stylesheet =
    CombinedStylesheet(listOf(this, other))