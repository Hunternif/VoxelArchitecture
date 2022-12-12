package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    ctx: DomContext,
    private val offset: Vec3,
) : DomBuilder(ctx) {
    override fun build(bldCtx: DomBuildContext) = guard {
        val offset = offset
        val childCtx = bldCtx.makeChildCtx()
        children.forEach {
            stylesheet.add {
                styleFor(it) {
                    position { origin, _ -> origin + offset }
                }
            }
            it.build(childCtx)
        }
    }
}