package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.CycleCounter
import hunternif.voxarch.util.Recursive

/**
 * Allows running more generators on the output of this generator.
 */
abstract class ChainedGenerator : Recursive(cycleCounter), IGenerator {
    val nextGens = LinkedHashSet<IGenerator>()

    /**
     * Add new DOM elements and run the next generators
     */
    abstract fun generateChained(
        parent: DomBuilder,
        parentNode: Node,
        nextBlock: DomBuilder.() -> Unit,
    )

    override fun generate(parent: DomBuilder, parentNode: Node) = guard {
        parent.apply {
            generateChained(this, parentNode) {
                nextGens.forEach { it.generate(this, parentNode) }
            }
        }
    }

    fun clearRecursionCounters() {
        cycleCounter.clear()
    }

    companion object {
        val cycleCounter = CycleCounter(20)
    }
}