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
        // Reset stylesheet, but don't write this into history
        ResetStylesheet().invoke(app)
        clearNewNodeFrame()
        redrawNodes()
        redrawVoxels()
        centerCamera()
    }

    override fun revert(app: EditorAppImpl) {
        // can't be reverted
    }
}