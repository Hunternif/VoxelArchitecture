package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.magicavoxel.readVoxFile
import java.nio.file.Path

class ImportVoxFile(
    private val path: Path
) : HistoryAction(
    "Import ${path.fileName}",
    FontAwesomeIcons.File
) {
    private lateinit var voxelGroup: SceneVoxelGroup

    override fun invoke(app: EditorAppImpl) = app.run {
        if (!::voxelGroup.isInitialized) {
            val file = readVoxFile(path)
            voxelGroup = SceneVoxelGroup(path.fileName.toString(), file)
        }
        state.run {
            sceneObjects.add(voxelGroup)
            voxelRoot.addChild(voxelGroup)
        }
        scene.updateVoxelModel()
    }

    override fun revert(app: EditorAppImpl) = app.run {
        state.run {
            sceneObjects.remove(voxelGroup)
            selectedObjects.remove(voxelGroup)
            hiddenObjects.remove(voxelGroup)
            manuallyHiddenObjects.remove(voxelGroup)
            voxelGroup.parent?.removeChild(voxelGroup)
        }
        scene.updateVoxelModel()
    }

}