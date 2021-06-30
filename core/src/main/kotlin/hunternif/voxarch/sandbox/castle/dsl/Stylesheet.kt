package hunternif.voxarch.sandbox.castle.dsl

import com.google.common.collect.ArrayListMultimap

/** Defines a sequence of cosmetic modifications to a DOM element. */
typealias StyleRule = StyledNode.() -> Unit

/** Represents a DOM element for the purpose of styling. */
@CastleDsl
class StyledNode(
    internal val domBuilder: DomBuilder,
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
    private val styles = ArrayListMultimap.create<String, StyleRule>()

    /** Register a style for the given class name. */
    fun style(styleClass: String, block: StyleRule) {
        styles.put(styleClass, block)
    }

    internal fun apply(domBuilder: DomBuilder, styleClass: Array<out String>) {
        val styled = StyledNode(domBuilder, domBuilder.seed)
        styleClass.flatMap { styles[it] }.forEach { it.invoke(styled) }
    }
}

