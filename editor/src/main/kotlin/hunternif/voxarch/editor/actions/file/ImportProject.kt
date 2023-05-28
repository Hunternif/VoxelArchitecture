package hunternif.voxarch.editor.actions.file

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.blueprint.BlueprintRegistry
import hunternif.voxarch.editor.file.readBlueprints
import hunternif.voxarch.editor.file.readMetadata
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.util.newZipFileSystem
import java.nio.file.Path

class ImportProject(
    private val path: Path,
    private val importBlueprints: Boolean = false,
) : HistoryAction(
    "Import project '${path.fileName}'",
    FontAwesomeIcons.FileImport
) {
    private val importedRegistry = BlueprintRegistry()

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) {
        if (firstTime) {
            val zipfs = newZipFileSystem(path)
            zipfs.use {
                readMetadata(path, zipfs, app)
                readBlueprints(zipfs, importedRegistry, mutableMapOf(), app)
            }
        }
        if (importBlueprints) {
            for (bp in importedRegistry.blueprints) {
                app.state.blueprintRegistry.save(bp)
            }
        }
    }

    override fun revert(app: EditorAppImpl) {
        if (importBlueprints) {
            for (bp in importedRegistry.blueprints) {
                app.state.blueprintRegistry.remove(bp)
            }
        }
    }
}