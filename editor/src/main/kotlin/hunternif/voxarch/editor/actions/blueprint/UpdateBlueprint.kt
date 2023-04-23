package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class UpdateBlueprint(
    val bp: Blueprint,
    val newName: String,
) : HistoryAction(
    "Rename blueprint",
    FontAwesomeIcons.Code
) {
    private val oldName = bp.name

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        bp.name = newName
    }

    override fun revert(app: EditorAppImpl) {
        bp.name = oldName
    }
}