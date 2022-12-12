package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.room

@PublicGenerator("Add room")
class GenAddRoom(vararg styleClass: String) : ChainedGenerator() {
    private val styles = styleClass

    override fun generateChained(
        bldCtx: DomBuildContext,
        nextBlock: DomBuilder.() -> Unit,
    ) {
        bldCtx.parent.room(*styles) {
            nextBlock()
        }
    }
}