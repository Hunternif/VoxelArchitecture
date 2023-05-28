package hunternif.voxarch.editor.actions.blueprint

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import hunternif.voxarch.editor.actions.logWarning
import hunternif.voxarch.editor.blueprint.Blueprint
import hunternif.voxarch.editor.gui.FontAwesomeIcons

class RenameBlueprint(
    val bp: Blueprint,
    var newName: String,
) : HistoryAction(
    "Rename blueprint",
    FontAwesomeIcons.Code
), StackingAction<RenameBlueprint> {
    private val oldName = bp.name

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            newName = newName.trim()
            if (app.state.blueprintLibrary.blueprintsByName[newName] != null) {
                app.logWarning("Duplicate blueprint name $newName")
            }
        }
        bp.name = newName
        app.state.blueprintRegistry.removeByName(oldName)
        app.state.blueprintRegistry.save(bp)
    }

    override fun revert(app: EditorAppImpl) {
        bp.name = oldName
        app.state.blueprintRegistry.removeByName(newName)
        app.state.blueprintRegistry.save(bp)
    }

    override fun update(nextAction: RenameBlueprint) {
        this.newName = nextAction.newName
    }
}