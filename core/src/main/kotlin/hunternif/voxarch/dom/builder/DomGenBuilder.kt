package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledGen
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.Node

/** Represents any nodes below the root. */
open class DomGenBuilder<G : IGenerator>(
    ctx: DomContext,
    val gen: G,
) : DomBuilder(ctx) {

    override fun build(parentNode: Node) = guard {
        val styled = StyledGen(gen, parentNode, this)
        stylesheet.applyStyle(styled, styleClass)
        if (visibility == Visibility.VISIBLE) {
            gen.generate(this, parentNode)
            children.forEach { it.build(parentNode) }
        }
    }
}