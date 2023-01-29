package hunternif.voxarch.dom.builder

import hunternif.voxarch.dom.style.StyledElement
import hunternif.voxarch.dom.style.property.*
import hunternif.voxarch.dom.style.selectInherit
import hunternif.voxarch.vector.Vec3

class DomTranslateBuilder(
    private val offset: Vec3,
) : DomBuilder() {
    override fun prepareForLayout(ctx: DomBuildContext): StyledElement<*> {
        children.forEach {
            ctx.stylesheet.add {
                style(selectInherit(this@DomTranslateBuilder).instance(it)) {
                    position { origin, _ -> origin + offset }
                }
            }
        }
        return super.prepareForLayout(ctx)
    }
}