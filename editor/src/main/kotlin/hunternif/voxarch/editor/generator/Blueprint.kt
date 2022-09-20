package hunternif.voxarch.editor.generator

import hunternif.voxarch.generator.IGenerator

/**
 * Represents a graph of DOM Generators.
 */
class Blueprint(
    var name: String,
) {
    val nodes = LinkedHashSet<BlueprintNode>()
    var start: BlueprintNode? = null
}

class BlueprintNode(
    val generator: IGenerator,
) {
    var input: BlueprintNode? = null
    val outputs = mutableListOf<BlueprintNode>()
}