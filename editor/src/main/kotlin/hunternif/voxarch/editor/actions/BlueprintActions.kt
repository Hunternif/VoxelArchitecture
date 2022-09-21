package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintNode
import hunternif.voxarch.editor.blueprint.BlueprintSlot
import hunternif.voxarch.generator.ChainedGenerator

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))

fun EditorApp.newBlueprintNode(
    bp: Blueprint,
    generator: ChainedGenerator
): BlueprintNode {
    val action = BlueprintNewNode(bp, generator)
    historyAction(action)
    return action.node
}

fun EditorApp.linkBlueprintNodes(from: BlueprintSlot.Out, to: BlueprintSlot.In) =
    historyAction(BlueprintCreateLink(from, to))

