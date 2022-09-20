package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.plan.Node
import kotlin.collections.LinkedHashSet

/**
 * Represents a graph of DOM Generators.
 */
class Blueprint(
    var name: String,
) {
    val nodes = LinkedHashSet<BlueprintNode>()
    var start: BlueprintNode? = null

    fun addNode(node: BlueprintNode) {
        nodes.add(node)
        if (start == null) {
            start = node
        }
    }

    fun execute(root: Node) {
        start?.generator?.generateFinal(root)
    }
}

class BlueprintNode(
    val generator: ChainedGenerator,
) {
    var input: BlueprintNode? = null
    var output: BlueprintNode? = null
        private set

    fun connectOutput(output: BlueprintNode?) {
        // disconnect the previous one
        this.output?.let {
            it.input = null
            this.generator.nextGens.remove(it.generator)
        }
        this.output = output
        output?.let {
            it.input = this
            this.generator.nextGens.add(it.generator)
        }
    }
}