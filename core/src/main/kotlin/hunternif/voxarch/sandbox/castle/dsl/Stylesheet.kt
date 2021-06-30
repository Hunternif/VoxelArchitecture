package hunternif.voxarch.sandbox.castle.dsl

import com.google.common.collect.ArrayListMultimap


typealias StyleRule = StyledNode.() -> Unit

class StyledNode(
    internal val domBuilder: DomBuilder,
    internal var seed: Long = domBuilder.seed
) {
    /**
     * Use seed of the parent DOM builder to calculate random values.
     * This makes the _immediate_ children appear identical, but the children's
     * children seeds can still be different.
     */
    fun useParentSeed() {
        seed = domBuilder.parent.seed
    }
}

@CastleDsl
class Stylesheet {
    private val styles = ArrayListMultimap.create<String, StyleRule>()

    fun style(styleClass: String, block: StyleRule) {
        styles.put(styleClass, block)
    }

    internal fun apply(domBuilder: DomBuilder, styleClass: Array<out String>) {
        val styled = StyledNode(domBuilder, domBuilder.seed)
        styleClass.flatMap { styles[it] }.forEach { it.invoke(styled) }
    }
}

