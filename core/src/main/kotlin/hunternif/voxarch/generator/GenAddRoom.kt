package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.room
import hunternif.voxarch.plan.Node

@PublicGenerator("Add room")
class GenAddRoom(vararg styleClass: String) : ChainedGenerator() {
    private val styles = styleClass

    override fun generateChained(
        parent: DomBuilder,
        parentNode: Node,
        nextBlock: DomBuilder.() -> Unit,
    ) {
        parent.room(*styles) {
            nextBlock()
        }
    }
}