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
        stylesheet.applyStyle(styled)
        if (visibility == Visibility.VISIBLE) {
            gen.generate(this, bldCtx.parentNode)
            val childCtx = bldCtx.makeChildCtx()
            children.forEach { it.build(childCtx) }
        }
    }
}