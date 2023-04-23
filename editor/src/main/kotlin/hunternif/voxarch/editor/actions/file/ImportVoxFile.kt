package hunternif.voxarch.editor.actions.file

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.FileEvent
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.magicavoxel.readVoxFile
import java.nio.file.Path

class ImportVoxFile(
    private val path: Path
) : HistoryAction(
    "Import ${path.fileName}",
    FontAwesomeIcons.File
), FileEvent {
    private lateinit var voxelGroup: SceneVoxelGroup

    override fun invoke(app: EditorAppImpl, firstTime: Boolean) = app.run {
        if (!::voxelGroup.isInitialized) {
            val file = readVoxFile(path)
            voxelGroup = state.registry.newVoxelGroup(
                path.fileName.toString(), file, VoxelRenderMode.COLORED)
        }
        state.voxelRoot.addChild(voxelGroup)
        scene.updateVoxelModel()
    }

    override fun revert(app: EditorAppImpl) = app.run {
        voxelGroup.remove()
        state.run {
            voxelGroup.parent?.removeChild(voxelGroup)
        }
        scene.updateVoxelModel()
    }

}