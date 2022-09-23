package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

class GenPassthrough : ChainedGenerator() {
    override fun generateChained(
        parent: DomBuilder<Node?>,
        nextBlock: DomBuilder<Node?>.() -> Unit
    ) {
        parent.nextBlock()
    }
}