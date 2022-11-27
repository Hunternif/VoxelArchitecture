package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node
import kotlin.reflect.KClass

/** Defines a sequence of cosmetic modifications to a DOM element. */
typealias StyleRuleForNode<N> = StyledNode<N>.() -> Unit
typealias StyleRuleForGen<G> = StyledGen<G>.() -> Unit

@CastleDsl
abstract class StyledElement(
    internal open val domBuilder: DomBuilder,
    internal var seed: Long = domBuilder.seed,
) {
    /**
     * Use the seed of the parent [DomNodeBuilder] to calculate random values.
     * This makes the _immediate_ children appear identical, but the children's
     * children's seeds can still be different.
     */
    fun useParentSeed() {
        seed = domBuilder.parent.seed
    }
}

/** Represents a DOM element with a [Node] for the purpose of styling. */
@CastleDsl
class StyledNode<out N: Node>(
    override val domBuilder: DomNodeBuilder<N>,
) : StyledElement(domBuilder) {
    val node: N get() = domBuilder.node
}

/** Represents a DOM element with a [IGenerator] for the purpose of styling. */
@CastleDsl
class StyledGen<out G: IGenerator>(
    internal val gen: G,
    domBuilder: DomBuilder
) : StyledElement(domBuilder)

@CastleDsl
interface StyleParameter

/** Container for all styles in a DOM. */
@CastleDsl
open class Stylesheet {
    private val nodeStyleMap = ArrayListMultimap.create<String, TypedStyleRuleForNode<*>>()
    private val genStyleMap = ArrayListMultimap.create<String, TypedStyleRuleForGen<*>>()

    private val rules = ArrayListMultimap.create<String, Rule>()

    /**
     * Register a style rule i.e. a list of style declarations.
     * They are not limited by Node type, they will apply wherever possible.
     * @param styleClass is the "CSS class".
     */
    fun style2(
        styleClass: String,
        block: Rule.() -> Unit,
    ) {
        rules.put(styleClass, Rule(styleClass).apply(block))
    }

    /**
     * Register a style for the given "style class" name, for specific Java class
     * and its subclasses.
     */
    inline fun <reified N: Node> styleFor(
        styleClass: String,
        noinline block: StyleRuleForNode<N>
    ) {
        styleFor(N::class.java, styleClass, block)
    }

    /**
     * Register a style for all subclasses.
     */
    inline fun <reified N: Node> styleFor(
        noinline block: StyleRuleForNode<N>
    ) {
        styleFor(N::class.java, GLOBAL_STYLE, block)
    }

    /**
     * Register a style for all subclasses.
     */
    fun <N: Node> styleFor(
        nodeClass: Class<N>,
        block: StyleRuleForNode<N>,
    ) {
        styleFor(nodeClass, GLOBAL_STYLE, block)
    }

    /**
     * Register a style for all subclasses.
     */
    fun <N: Node> styleFor(
        nodeClass: KClass<N>,
        block: StyleRuleForNode<N>,
    ) {
        styleFor(nodeClass.java, GLOBAL_STYLE, block)
    }

    /** Register a style for the given "style class" name for all [Node] subclasses. */
    fun style(styleClass: String, block: StyleRuleForNode<Node>) {
        styleFor(Node::class.java, styleClass, block)
    }

    /**
     * Register a style for the given "style class" name, for specific Java class
     * and its subclasses.
     */
    fun <N: Node> styleFor(nodeClass: Class<N>, styleClass: String, block: StyleRuleForNode<N>) {
        nodeStyleMap.put(styleClass, TypedStyleRuleForNode(nodeClass, block))
    }

    /**
     * Register a style for the given "style class" name, for specific Java class
     * and its subclasses.
     */
    inline fun <reified G: IGenerator> styleForGen(
        styleClass: String,
        noinline block: StyleRuleForGen<G>
    ) {
        styleForGen(G::class.java, styleClass, block)
    }

    /**
     * Register a style for all subclasses.
     */
    inline fun <reified G: IGenerator> styleForGen(
        noinline block: StyleRuleForGen<G>
    ) {
        styleForGen(G::class.java, GLOBAL_STYLE, block)
    }

    /**
     * Register a style for the given "style class" name, for specific Java class
     * and its subclasses.
     */
    fun <G: IGenerator> styleForGen(
        genClass: Class<G>,
        styleClass: String,
        block: StyleRuleForGen<G>,
    ) {
        genStyleMap.put(styleClass, TypedStyleRuleForGen(genClass, block))
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
                .flatMap { genStyleMap[it] }
                .filter { it.genClass.isAssignableFrom(genClass) }
                .forEach {
                    @Suppress("UNCHECKED_CAST")
                    val rule = it.rule as StyleRuleForGen<*>
                    rule.invoke(styledGen)
                }
            // apply rules
            styleClasses
                .flatMap { rules[it] }
                .flatMap { it.declarations }
                .forEach { it.applyTo(styledGen) }
        }
        // apply node styles
        if (domBuilder is DomNodeBuilder<*>) {
            val styledNode = StyledNode(domBuilder)
            val nodeClass = domBuilder.node.javaClass
            styleClasses
                .flatMap { nodeStyleMap[it] }
                .filter { it.nodeClass.isAssignableFrom(nodeClass) }
                .forEach {
                    @Suppress("UNCHECKED_CAST")
                    val rule = it.rule as StyleRuleForNode<*>
                    rule.invoke(styledNode)
                }
            // apply rules
            styleClasses
                .flatMap { rules[it] }
                .flatMap { it.declarations }
                .forEach { it.applyTo(styledNode) }
        }
    }

    fun clear() {
        nodeStyleMap.clear()
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"

        private data class TypedStyleRuleForNode<N: Node>(
            val nodeClass: Class<N>,
            val rule: StyleRuleForNode<N>
        )

        private data class TypedStyleRuleForGen<G: IGenerator>(
            val genClass: Class<G>,
            val rule: StyleRuleForGen<G>
        )
    }
}

class CombinedStylesheet(private val stylesheets: Collection<Stylesheet>): Stylesheet() {
    override fun apply(
        domBuilder: DomBuilder,
        styleClass: Collection<String>
    ) {
        stylesheets.forEach { it.apply(domBuilder, styleClass) }
    }
}

operator fun Stylesheet.plus(other: Stylesheet): Stylesheet =
    CombinedStylesheet(listOf(this, other))