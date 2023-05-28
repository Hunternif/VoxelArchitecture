package hunternif.voxarch.editor.blueprint

import hunternif.voxarch.editor.AppState
import hunternif.voxarch.editor.scenegraph.SceneNode

/** Read-only interface for [BlueprintRegistry] */
interface IBlueprintLibrary {
    /** All Blueprints in the project */
    val blueprints: Collection<Blueprint>

    val blueprintsByName: Map<String, Blueprint>

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
    private val usageMap = mutableMapOf<Blueprint, Usage>()

    override val blueprints get() = blueprintsByName.values

    private val _blueprintsByName = LinkedHashMap<String, Blueprint>()
    override val blueprintsByName: Map<String, Blueprint>
        get() = _blueprintsByName

    fun newBlueprint(name: String): Blueprint {
        val newName = makeUniqueName(name)
        val blueprint = Blueprint(newName)
        save(blueprint)
        return blueprint
    }

    fun save(blueprint: Blueprint) {
        // If name is already taken, refresh name:
        if (blueprintsByName[blueprint.name] !== blueprint) {
            blueprint.name = makeUniqueName(blueprint.name)
        }
        _blueprintsByName[blueprint.name] = blueprint
    }

    fun remove(blueprint: Blueprint) {
        _blueprintsByName.remove(blueprint.name)
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

    /** In case of duplicates, changes "Untitled" to "Untitled (2)" */
    private fun makeUniqueName(name: String): String {
        if (blueprintsByName[name] == null) return name
        var i = 0
        var newName: String
        do {
            i++
            newName = "$name ($i)"
        }
        while (blueprintsByName[newName] != null)
        return newName
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
