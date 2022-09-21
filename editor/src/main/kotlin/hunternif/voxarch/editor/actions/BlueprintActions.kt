package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.blueprint.Blueprint

fun EditorApp.selectBlueprint(bp: Blueprint?) = historyAction(OpenBlueprint(bp))