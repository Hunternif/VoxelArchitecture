package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

class GenPassthrough : ChainedGenerator() {
    override fun generateChained(
        parent: DomBuilder,
        parentNode: Node,
        nextBlock: DomBuilder.() -> Unit
    ) {
        parent.nextBlock()
    }
}