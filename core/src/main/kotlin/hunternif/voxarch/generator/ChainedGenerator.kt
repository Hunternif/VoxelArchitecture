package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

/**
 * Allows running more generators on the output of this generator.
 */
abstract class ChainedGenerator : IGenerator {
    val nextGens = LinkedHashSet<IGenerator>()

    /**
     * Add new DOM elements and run the next generators
     */
    abstract fun generateChained(
        parent: DomBuilder<Node?>,
        nextBlock: DomBuilder<Node?>.() -> Unit,
    )

    override fun generate(parent: DomBuilder<Node?>) {
        generateChained(parent) {
            nextGens.forEach { it.generate(this) }
        }
    }
}