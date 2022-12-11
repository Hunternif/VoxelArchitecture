package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.plus
import hunternif.voxarch.plan.Node
import hunternif.voxarch.util.CycleCounter
import kotlin.collections.LinkedHashSet

/**
 * Allows running more generators on the output of this generator.
 */
abstract class ChainedGenerator : IGenerator {
    val nextGens = LinkedHashSet<IGenerator>()
    val localStyle = Stylesheet()
    var recursions = 0

    /**
     * Add new DOM elements and run the next generators
     */
    abstract fun generateChained(
        parent: DomBuilder,
        parentNode: Node,
        nextBlock: DomBuilder.() -> Unit,
    )

    override fun generate(parent: DomBuilder, parentNode: Node) {
        cycleCounter.guard(this) {
            parent.apply {
                // Add custom stylesheet
                val originalStyle = stylesheet
                stylesheet = originalStyle + localStyle
                generateChained(this, parentNode) {
                    // Restore original stylesheet
                    stylesheet = originalStyle
                    nextGens.forEach { it.generate(this, parentNode) }
                }
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