package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledGen
import hunternif.voxarch.generator.IGenerator

/** Represents any nodes below the root. */
open class DomGenBuilder<G : IGenerator>(
    val gen: G,
) : DomBuilder() {

    override fun build(ctx: DomBuildContext) = guard {
        val styled = StyledGen(gen, ctx.parentNode, this,
            ctx.seed + seedOffset)
            .inherit(ctx.inheritedStyleClass)
        ctx.stylesheet.applyStyle(styled)
        if (visibility == Visibility.VISIBLE) {
            val childCtx = ctx.makeChildCtx()
            gen.generate(childCtx)
            // the generator could have added styles:
            childCtx.inherit(styleClass)
            children.forEach { it.build(childCtx) }
        }
    }
}