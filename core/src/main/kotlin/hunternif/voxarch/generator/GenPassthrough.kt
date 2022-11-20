package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder

class GenPassthrough : ChainedGenerator() {
    override fun generateChained(
        parent: DomBuilder,
        nextBlock: DomBuilder.() -> Unit
    ) {
        parent.nextBlock()
    }
}