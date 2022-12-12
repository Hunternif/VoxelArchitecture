package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuildContext
import hunternif.voxarch.dom.builder.DomBuilder
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
        bldCtx: DomBuildContext,
        nextBlock: DomBuilder.() -> Unit,
    )

    override fun generate(bldCtx: DomBuildContext) = guard {
        generateChained(bldCtx) {
            //TODO: the parent node here may be wrong
            val childCtx = bldCtx.makeChildCtx()
            nextGens.forEach { it.generate(childCtx) }
        }
    }

    fun clearRecursionCounters() {
        cycleCounter.clear()
    }

    companion object {
        val cycleCounter = CycleCounter(20)
    }
}