package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledGen
import hunternif.voxarch.generator.IGenerator

/** Represents any nodes below the root. */
open class DomGenBuilder<G : IGenerator>(
    ctx: DomContext,
    val gen: G,
) : DomBuilder(ctx) {

    override fun build(bldCtx: DomBuildContext) = guard {
        val styled = StyledGen(gen, bldCtx.parentNode, this)
            .inherit(bldCtx.inheritedStyleClass)
        stylesheet.applyStyle(styled)
        if (visibility == Visibility.VISIBLE) {
            val childCtx = bldCtx.makeChildCtx()
            gen.generate(childCtx)
            // the generator could have added styles:
            childCtx.inherit(styleClass)
            children.forEach { it.build(childCtx) }
        }
    }
}