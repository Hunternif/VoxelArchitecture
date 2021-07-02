package hunternif.voxarch.dom

import com.google.common.collect.ArrayListMultimap
import hunternif.voxarch.plan.Node

/** Defines a sequence of cosmetic modifications to a DOM element. */
typealias StyleRule<N> = StyledNode<N>.() -> Unit

/** Represents a DOM element for the purpose of styling. */
@CastleDsl
class StyledNode<out N: Node>(
    internal val domBuilder: DomBuilder<N>,
    internal var seed: Long = domBuilder.seed
) {
    /**
     * Use the seed of the parent [DomBuilder] to calculate random values.
     * This makes the _immediate_ children appear identical, but the children's
     * children's seeds can still be different.
     */
    fun useParentSeed() {
        seed = domBuilder.parent.seed
    }
}

/** Container for all styles in a DOM. */
@CastleDsl
class Stylesheet {
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
     * Register a style for for all subclasses.
     */
    inline fun <reified N: Node> styleFor(
        noinline block: StyleRule<N>
    ) {
        styleFor(N::class.java, GLOBAL_STYLE, block)
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

    internal fun <N: Node> apply(
        domBuilder: DomBuilder<N>,
        styleClass: Array<out String>
    ) {
        val styled = StyledNode(domBuilder, domBuilder.seed)
        val nodeClass = domBuilder.node?.javaClass ?: Node::class.java
        (styleClass.toMutableList() + GLOBAL_STYLE)
            .flatMap { styleMap[it] }
            .filter { it.nodeClass.isAssignableFrom(nodeClass) }
            .forEach {
                @Suppress("UNCHECKED_CAST")
                val rule = it.rule as StyleRule<Node>
                rule.invoke(styled)
            }
    }

    companion object {
        const val GLOBAL_STYLE = "__global_style__"

        private data class TypedStyleRule<N: Node>(
            val nodeClass: Class<N>,
            val rule: StyleRule<N>
        )
    }
}

