package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.generator.IGenerator


fun EditorApp.newBlueprint(node: SceneNode) = action {
    addBlueprint(node, state.registry.newBlueprint("Untitled"), true)
}

fun EditorApp.addBlueprint(
    node: SceneNode, blueprint: Blueprint, autoSelect: Boolean = false
) {
    val newSelected = if (autoSelect) blueprint else state.selectedBlueprint
    historyAction(SetBlueprints(
        node, node.blueprints + blueprint, newSelected,
        "Add blueprint", FontAwesomeIcons.Landmark
    ))
}

fun EditorApp.removeBlueprint(node: SceneNode, blueprint: Blueprint) {
    val newSelected = state.selectedBlueprint?.let {
        if (it == blueprint) null else it
    }
    val newBlues = node.blueprints.toMutableList()
    if (newBlues.remove(blueprint)) {
        historyAction(SetBlueprints(
            node, newBlues, newSelected,
            "Remove blueprint", FontAwesomeIcons.TrashAlt,
        ))
    }
}

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))

fun EditorApp.newBlueprintNode(
    bp: Blueprint,
    name: String,
    generator: IGenerator,
    x: Float = 0f,
    y: Float = 0f,
): BlueprintNode {
    val action = BlueprintNewNode(bp, name, generator, x, y)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteBlueprintNode(node: BlueprintNode) =
    historyAction(BlueprintDeleteNode(node))

fun EditorApp.linkBlueprintSlots(from: BlueprintSlot.Out, to: BlueprintSlot.In) =
    historyAction(BlueprintCreateLink(from, to))

fun EditorApp.unlinkBlueprintLink(link: BlueprintLink) =
    historyAction(BlueprintUnlink(link.from, link.to))

fun EditorApp.deleteBlueprintParts(
    nodes: Collection<BlueprintNode>,
    links: Collection<BlueprintLink>,
) = historyAction(BlueprintDeleteParts(nodes, links))