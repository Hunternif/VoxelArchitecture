package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.file.readProject
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.newState
import java.nio.file.Path

class OpenProject(private val path: Path) : HistoryAction(
    "Open project '${path.fileName}'",
    FontAwesomeIcons.File
) {
    override fun invoke(app: EditorAppImpl) = app.run {
        readProject(path)
        redrawNodes()
        redrawVoxels()
        centerCamera()
    }

    override fun revert(app: EditorAppImpl) {
        // can't be reverted
    }
}