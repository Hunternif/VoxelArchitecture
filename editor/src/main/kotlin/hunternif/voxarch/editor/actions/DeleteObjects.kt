package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.*

class DeleteObjects(
    objs: Collection<SceneObject>
) : HistoryAction(makeDescription(objs), FontAwesomeIcons.TrashAlt) {
    companion object {
        private fun makeDescription(objs: Collection<SceneObject>) =
            when (objs.size) {
                1 -> "Delete"
                else -> "Delete ${objs.size} objects"
            }
    }

    private val detachedObjs = objs.map { it.detached() }

    private val hasNodes = objs.any { it is SceneNode }
    private val hasVoxels = objs.any { it is SceneVoxelGroup }

    override fun invoke(app: EditorAppImpl) = app.state.run {
        for (detached in detachedObjs) {
            detached.detach()
            // Parent node must not point to a deleted node:
            if (detached.obj === parentNode) parentNode = rootNode
        }
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) = app.state.run {
        for (detached in detachedObjs) {
            detached.reattach()
        }
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }
}