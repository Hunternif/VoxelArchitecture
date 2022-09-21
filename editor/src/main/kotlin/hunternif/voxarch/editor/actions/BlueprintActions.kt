package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintLink
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.generator.ChainedGenerator


fun EditorApp.addBlueprint(node: SceneNode, blueprint: Blueprint) {
    historyAction(SetBlueprints(
        node, node.blueprints + blueprint,
        "Add blueprint", FontAwesomeIcons.Landmark
    ))
}

fun EditorApp.removeBlueprint(node: SceneNode, blueprint: Blueprint) {
    if (state.selectedBlueprint == blueprint) {
        selectBlueprint(null)
    }
    val newBlues = node.blueprints.toMutableList()
    if (newBlues.remove(blueprint)) {
        historyAction(SetBlueprints(
            node, newBlues,
            "Remove blueprint", FontAwesomeIcons.TrashAlt
        ))
    }
}

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))

fun EditorApp.newBlueprintNode(
    bp: Blueprint,
    generator: ChainedGenerator
): BlueprintNode {
    val action = BlueprintNewNode(bp, generator)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteBlueprintNode(node: BlueprintNode) =
    historyAction(BlueprintDeleteNode(node))

fun EditorApp.linkBlueprintSlots(from: BlueprintSlot.Out, to: BlueprintSlot.In) =
    historyAction(BlueprintCreateLink(from, to))

fun EditorApp.unlinkBlueprintSlot(link: BlueprintLink) =
    historyAction(BlueprintUnlink(link))