package hunternif.voxarch.editor.generator

import hunternif.voxarch.generator.IGenerator
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
        start?.let { it.generator.generateFinal(root) }
    }
}

class BlueprintNode(
    val generator: IGenerator,
) {
    var input: BlueprintNode? = null
    val outputs = mutableListOf<BlueprintNode>()
}