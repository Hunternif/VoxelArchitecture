package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scenegraph.*
import java.util.*

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

    // copy the list in case the original is modified, e.g. [selectedObjects]
    private val objs = objs.toMutableList().apply {
        // recursively add child objects, and prevent cycles:
        val visited = mutableSetOf<SceneObject>()
        val queue = LinkedList(this)

        while (queue.isNotEmpty()) {
            val item = queue.removeLast()
            if (item !in visited) {
                visited.add(item)
                this.add(item)
                queue.addAll(item.children)
            }
        }
    }

    private val detachedObjs = objs.map { it.detached() }

    private val previouslyHidden = mutableSetOf<SceneObject>()
    private val previouslySelected = mutableSetOf<SceneObject>()

    private val hasNodes = objs.any { it is SceneNode }
    private val hasVoxels = objs.any { it is SceneVoxelGroup }

    override fun invoke(app: EditorAppImpl) = app.state.run {
        for (detached in detachedObjs) {
            detached.detach()
        }
        for (obj in objs) {
            if (obj === rootNode) continue
            // Parent node must not point to a deleted node:
            if (obj === parentNode) parentNode = rootNode
            if (manuallyHiddenObjects.remove(obj)) previouslyHidden.add(obj)
            if (selectedObjects.remove(obj)) previouslySelected.add(obj)
        }
        app.updateHiddenObjects()
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) = app.state.run {
        for (detached in detachedObjs) {
            detached.reattach()
        }
        for (obj in objs) {
            if (obj in previouslySelected) selectedObjects.add(obj)
            if (obj in previouslyHidden) manuallyHiddenObjects.add(obj)
        }
        app.updateHiddenObjects()
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }
}