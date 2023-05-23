package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.AppState
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.util.IDRegistry

/** Read-only interface for [BlueprintRegistry] */
interface IBlueprintLibrary {
    /** All Blueprints in the project */
    val blueprints: Collection<Blueprint>

    val blueprintsByName: Map<String, Blueprint>

    val blueprintsByID: Map<Int, Blueprint>

    /** Maps Blueprint to nodes where it's used */
    fun usage(bp: Blueprint): BlueprintRegistry.Usage
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
    private val blueprintIDs = IDRegistry<Blueprint>()

    private val usageMap = mutableMapOf<Blueprint, Usage>()

    override val blueprints get() = blueprintIDs.map.values

    private val _blueprintsByName = mutableMapOf<String, Blueprint>()
    override val blueprintsByName: Map<String, Blueprint>
        get() = _blueprintsByName

    override val blueprintsByID: Map<Int, Blueprint> get() = blueprintIDs.map

    fun newBlueprint(name: String): Blueprint {
        val id = blueprintIDs.newID()
        // In case of duplicates, change "Untitled" to "Untitled (2)"
        val nameExists = blueprintIDs.map.values.any { it.name == name }
        val newName = if (nameExists) "$name ($id)" else name
        val blueprint = Blueprint(id, newName)
        save(blueprint)
        return blueprint
    }

    fun save(blueprint: Blueprint) {
        blueprintIDs.save(blueprint)
        refreshMapByName()
    }

    fun remove(blueprint: Blueprint) {
        blueprintIDs.remove(blueprint)
        refreshMapByName()
    }

    override fun usage(bp: Blueprint): Usage =
        usageMap.getOrPut(bp) { Usage(bp) }

    /** Record that [bp] is used in this scene node */
    fun addUsage(bp: Blueprint, node: SceneNode) {
        usage(bp)._nodes.add(node)
    }

    /** Record that [bp] is used in this BP node in [DomRunBlueprint] */
    fun addUsage(bp: Blueprint, delegator: BlueprintNode) {
        usage(bp)._delegators.add(delegator)
    }

    /** Record that [bp] is used in this scene node */
    fun removeUsage(bp: Blueprint, node: SceneNode) {
        usage(bp)._nodes.remove(node)
    }

    /** Record that [bp] is used in this BP node in [DomRunBlueprint] */
    fun removeUsage(bp: Blueprint, delegator: BlueprintNode) {
        usage(bp)._delegators.remove(delegator)
    }

    /** Find all references and fix any inconsistencies. */
    fun refreshUsages(state: AppState) {
        refreshUsagesInNodes(state)
        refreshUsagesInBlueprints()
    }

    /** Refresh usages in scene nodes */
    fun refreshUsagesInNodes(state: AppState) {
        usageMap.values.forEach { it._nodes.clear() }
        state.rootNode.iterateSubtree().forEach { o ->
            if (o is SceneNode) {
                o.blueprints.forEach { addUsage(it, o) }
            }
        }
    }

    /** Refresh usages in blueprint delegate nodes */
    fun refreshUsagesInBlueprints() {
        usageMap.values.forEach { it._delegators.clear() }
        blueprints.forEach { bp ->
            bp.nodes.forEach { node ->
                if (node.domBuilder is DomRunBlueprint) {
                    addUsage(node.domBuilder.blueprint, node)
                }
            }
        }
    }

    fun refreshMapByName() {
        _blueprintsByName.apply {
            clear()
            blueprints.forEach { put(it.name, it) }
        }
    }

    /** Record of where this blueprint is used in the project */
    class Usage(val bp: Blueprint) {
        internal val _nodes = LinkedHashSet<SceneNode>()
        /** Nodes which have this blueprint */
        val nodes: Collection<SceneNode>
            get() = _nodes

        internal val _delegators = LinkedHashSet<BlueprintNode>()
        /** "Blueprint" nodes in other blueprints that use this blueprint */
        val delegators: Collection<BlueprintNode>
            get() = _delegators

        val totalUsages get() = nodes.size + delegators.size
    }
}
