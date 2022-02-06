package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.magicavoxel.readVoxFile
import java.nio.file.Path

class ImportVoxFile(
    private val path: Path
) : HistoryAction("Import VOX file ${path.fileName}") {

    private lateinit var voxelGroup: SceneVoxelGroup

    override fun invoke(app: EditorAppImpl) = app.run {
        val file = readVoxFile(path)
        state.run {
            voxelGroup = SceneVoxelGroup(path.fileName.toString(), file)
            sceneObjects.add(voxelGroup)
            voxelRoot.addChild(voxelGroup)
        }
        scene.updateVoxelModel()
    }

    override fun revert(app: EditorAppImpl) = app.run {
        state.run {
            sceneObjects.remove(voxelGroup)
            hiddenObjects.remove(voxelGroup)
            voxelGroup.parent?.removeChild(voxelGroup)
        }
        scene.updateVoxelModel()
        scene.updateSelectedNodeModel()
    }

}