package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.selectInherit
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    private val offset: Vec3,
) : DomBuilder() {
    override fun build(ctx: DomBuildContext) = guard {
        val offset = offset
        val childCtx = ctx.makeChildCtx()
        children.forEach {
            ctx.stylesheet.add {
                style(selectInherit(this@DomTranslateBuilder).instance(it)) {
                    position { origin, _ -> origin + offset }
                }
            }
            it.build(childCtx)
        }
    }
}