package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node

/**
 * Allows running more generators on the output of this generator.
 */
abstract class ChainedGenerator : IGenerator {
    val nextGens = LinkedHashSet<IGenerator>()
    var recursions = 0

    /**
     * Add new DOM elements and run the next generators
     */
    abstract fun generateChained(
        parent: DomBuilder<Node?>,
        nextBlock: DomBuilder<Node?>.() -> Unit,
    )

    override fun generate(parent: DomBuilder<Node?>) {
        recursions++
        if (recursions > RECURSION_CAP) return
        generateChained(parent) {
            nextGens.forEach { it.generate(this) }
        }
    }

    companion object {
        const val RECURSION_CAP = 20
    }
}