package hunternif.voxarch.editor.actions.file

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.AppAction
import hunternif.voxarch.editor.actions.logWarning
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.copyTo
import hunternif.voxarch.util.forEachSubtree
import java.nio.file.Path

class ExportVoxFile(private val path: Path) : AppAction {
    override fun invoke(app: EditorAppImpl) = app.run {
        // Copy all vox groups into a single storage
        //TODO: serialize separate VOX groups
        val mergedStorage = ChunkedStorage3D<IVoxel>()
        state.voxelRoot.forEachSubtree {
            if (it is SceneVoxelGroup) {
                it.data.copyTo(mergedStorage, it.findGlobalPosition().toIntVec3())
            }
        }
        mergedStorage.writeToVoxFile(path) { v ->
            when (v) {
                is VoxColor -> v
                else -> VoxColor(state.voxelColorMap(v).hex)
            }
        }
        logWarning("Export complete to '$path'")
    }
}