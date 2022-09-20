package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.util.IDRegistry
import hunternif.voxarch.editor.util.WithID
import hunternif.voxarch.generator.ChainedGenerator
import hunternif.voxarch.plan.Node
import kotlin.collections.LinkedHashSet

/**
 * Represents a graph of DOM Generators.
 */
class Blueprint(
    override val id: Int,
    var name: String,
) : WithID {
    val nodes = LinkedHashSet<BlueprintNode>()
    var start: BlueprintNode? = null

    private val registry = IDRegistry<BlueprintNode>()

    fun newNode(generator: ChainedGenerator): BlueprintNode {
        val node = BlueprintNode(registry.newID(), generator)
        nodes.add(node)
        if (start == null) {
            start = node
        }
        return node
    }

    fun execute(root: Node) {
        start?.generator?.generateFinal(root)
    }
}

class BlueprintNode(
    override val id: Int,
    val generator: ChainedGenerator,
) : WithID {
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