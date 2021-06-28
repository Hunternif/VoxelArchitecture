package hunternif.voxarch.sandbox.castle.dsl

import com.google.common.collect.ArrayListMultimap
import hunternif.voxarch.plan.Node


typealias StyleRule = StyledNode.() -> Unit

class StyledNode(val node: Node, val seed: Long)

@CastleDsl
class Stylesheet {
    private val styles = ArrayListMultimap.create<String, StyleRule>()

    fun style(styleClass: String, block: StyleRule) {
        styles.put(styleClass, block)
    }

    internal fun apply(domBuilder: DomBuilder, styleClass: Array<out String>) {
        val node = domBuilder.node ?: return
        val styled = StyledNode(node, domBuilder.seed)
        styleClass.flatMap { styles[it] }.forEach { it.invoke(styled) }
    }
}

