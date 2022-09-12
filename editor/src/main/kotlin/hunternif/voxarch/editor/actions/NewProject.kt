package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.newState

class NewProject() : HistoryAction(
    "New project",
    FontAwesomeIcons.File
) {
    override fun invoke(app: EditorAppImpl) = app.run {
        state = newState()
        redrawNodes()
        redrawVoxels()
        centerCamera()
    }

    override fun revert(app: EditorAppImpl) {
        // can't be reverted
    }
}