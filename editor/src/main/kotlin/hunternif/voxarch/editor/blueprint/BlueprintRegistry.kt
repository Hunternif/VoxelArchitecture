package hunternif.voxarch.editor.blueprint

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import hunternif.voxarch.editor.AppState
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.IDRegistry

/** Read-only interface for [BlueprintRegistry] */
interface IBlueprintLibrary {
    /** All Blueprints in the project */
    val blueprints: Collection<Blueprint>

    /** Maps Blueprint to nodes where it's used */
    fun usageInNodes(bp: Blueprint): List<SceneNode>
}

/**
 * Keeps track of where blueprints are referenced:
 * - in SceneNodes
 * - in Blueprint Delegate nodes in other Blueprints
 *
 * Also keeps track of output slots in Blueprints, because delegates need
 * to be updated.
 *
 * Call [refreshUsages] to find all references and fix any inconsistencies.
 */
class BlueprintRegistry : IBlueprintLibrary {
    val blueprintIDs = IDRegistry<Blueprint>()

    /** Maps BP to nodes where it's used */
    private val bpInNodes: ListMultimap<Blueprint, SceneNode> = ArrayListMultimap.create()

    override val blueprints get() = blueprintIDs.map.values

    fun newBlueprint(name: String): Blueprint {
        val id = blueprintIDs.newID()
        // In case of duplicates, change "Untitled" to "Untitled (2)"
        val nameExists = blueprintIDs.map.values.any { it.name == name }
        val newName = if (nameExists) "$name ($id)" else name
        val blueprint = Blueprint(id, newName)
        blueprintIDs.save(blueprint)
        return blueprint
    }

    override fun usageInNodes(bp: Blueprint): List<SceneNode> = bpInNodes.get(bp)

    /** Record that a blueprint is used in this node */
    fun addUsage(bp: Blueprint, node: SceneNode) {
        bpInNodes.put(bp, node)
    }

    /** Record that a blueprint is used in this node */
    fun removeUsage(bp: Blueprint, node: SceneNode) {
        bpInNodes.remove(bp, node)
    }

    /** Find all references and fix any inconsistencies. */
    fun refreshUsages(state: AppState) {
        bpInNodes.clear()
        state.rootNode.iterateSubtree().forEach { o ->
            if (o is SceneNode) {
                o.blueprints.forEach { addUsage(it, o) }
            }
        }
    }
}