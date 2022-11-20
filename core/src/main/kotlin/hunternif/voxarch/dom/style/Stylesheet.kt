package hunternif.voxarch.dom.style

import com.google.common.collect.ArrayListMultimap
import hunternif.voxarch.dom.CastleDsl
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.builder.DomNodeBuilder
import hunternif.voxarch.plan.Node
import kotlin.reflect.KClass

/** Defines a sequence of cosmetic modifications to a DOM element. */
typealias StyleRule<N> = StyledNode<N>.() -> Unit

/** Represents a DOM element for the purpose of styling. */
@CastleDsl
class StyledNode<out N: Node>(
    internal val domBuilder: DomNodeBuilder<N>,
    internal var seed: Long = domBuilder.seed
) {
    val node: N get() = domBuilder.node
    /**
     * Use the seed of the parent [DomNodeBuilder] to calculate random values.
     * This makes the _immediate_ children appear identical, but the children's
     * children's seeds can still be different.
     */
    fun useParentSeed() {
        seed = domBuilder.parent.seed
    }
}

@CastleDsl
interface StyleParameter

/** Container for all styles in a DOM. */
@CastleDsl
open class Stylesheet {
    private val styleMap = ArrayListMultimap.create<String, TypedStyleRule<*>>()

    /**
     * Register a style for the given class name, for specific Java class
     * and its subclasses.
     */
    inline fun <reified N: Node> styleFor(
        styleClass: String,
        noinline block: StyleRule<N>
    ) {
        styleFor(N::class.java, styleClass, block)
    }

    /**
     * Register a style for all subclasses.
     */
    inline fun <reified N: Node> styleFor(
        noinline block: StyleRule<N>
    ) {
        styleFor(N::class.java, GLOBAL_STYLE, block)
    }

    /**
     * Register a style for all subclasses.
     */
    fun <N: Node> styleFor(
        nodeClass: Class<N>,
        block: StyleRule<N>,
    ) {
        styleFor(nodeClass, GLOBAL_STYLE, block)
    }

    /**
     * Register a style for all subclasses.
     */
    fun <N: Node> styleFor(
        nodeClass: KClass<N>,
        block: StyleRule<N>,
    ) {
        styleFor(nodeClass.java, GLOBAL_STYLE, block)
    }

    /** Register a style for the given class name for all [Node] subclasses. */
    fun style(styleClass: String, block: StyleRule<Node>) {
        styleFor(Node::class.java, styleClass, block)
    }

    /**
     * Register a style for the given class name, for specific Java class
     * and its subclasses.
     */
    fun <N: Node> styleFor(nodeClass: Class<N>, styleClass: String, block: StyleRule<N>) {
        styleMap.put(styleClass, TypedStyleRule(nodeClass, block))
    }

    internal open fun apply(
        domBuilder: DomBuilder,
        styleClass: Collection<String>
    ) {
        if (domBuilder is DomNodeBuilder<*>) {
            val styled = StyledNode(domBuilder, domBuilder.seed)
            val nodeClass = domBuilder.node.javaClass
            (listOf(GLOBAL_STYLE) + styleClass)
                .flatMap { styleMap[it] }
                .filter { it.nodeClass.isAssignableFrom(nodeClass) }
                .forEach {
                    @Suppress("UNCHECKED_CAST")
                    val rule = it.rule as StyleRule<Node>
                    rule.invoke(styled)
                }
        }
    }

    fun clear() {
        styleMap.clear()
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"

        private data class TypedStyleRule<N: Node>(
            val nodeClass: Class<N>,
            val rule: StyleRule<N>
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