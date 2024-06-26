package hunternif.voxarch.editor.actions.file

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.style.ResetStylesheet
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.newState

class NewProject() : HistoryAction(
    "New project",
    FontAwesomeIcons.File
) {
    override fun invoke(app: EditorAppImpl, firstTime: Boolean) = app.run {
        state = newState()
        gui.initState()
        // Reset stylesheet, but don't write this into history
        ResetStylesheet().invoke(app, true)
        reloadStyleEditor()
        clearNewNodeFrame()
        redrawNodes()
        redrawVoxels()
        centerCamera()
    }

    override fun revert(app: EditorAppImpl) {
        // can't be reverted
    }
}