package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    private val offset: Vec3,
) : DomBuilder() {
    override fun build(bldCtx: DomBuildContext) = guard {
        val offset = offset
        val childCtx = bldCtx.makeChildCtx()
        children.forEach {
            bldCtx.stylesheet.add {
                styleFor(it) {
                    position { origin, _ -> origin + offset }
                }
            }
            it.build(childCtx)
        }
    }
}