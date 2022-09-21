package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.blueprint.BlueprintSlot

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))

fun EditorApp.linkBlueprintNodes(from: BlueprintSlot.Out, to: BlueprintSlot.In) =
    historyAction(BlueprintCreateLink(from, to))

