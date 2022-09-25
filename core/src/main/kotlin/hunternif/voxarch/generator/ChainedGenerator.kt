package hunternif.voxarch.generator

import hunternif.voxarch.dom.builder.DomBuilder
import hunternif.voxarch.dom.newRoot
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.dom.style.plus
import hunternif.voxarch.plan.Node
import java.util.*
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
        parent: DomBuilder<Node?>,
        nextBlock: DomBuilder<Node?>.() -> Unit,
    )

    override fun generate(parent: DomBuilder<Node?>) {
        recursions++
        if (recursions > RECURSION_CAP) return
        parent.apply {
            // Add custom stylesheet
            val originalStyle = stylesheet
            newRoot(originalStyle + localStyle) {
                generateChained(this) {
                    // Restore original stylesheet
                    newRoot(originalStyle) {
                        nextGens.forEach { it.generate(this) }
                    }
                }
            }
        }
    }

    fun clearRecursionCounters() {
        val visited = mutableSetOf<ChainedGenerator>()
        val queue = LinkedList<ChainedGenerator>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            next.recursions = 0
            visited.add(next)
            next.nextGens.forEach {
                if (it !in visited && it is ChainedGenerator)
                    queue.add(it)
            }
        }
    }

    companion object {
        const val RECURSION_CAP = 20
    }
}