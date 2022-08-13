package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.util.INested
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
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
        val queue = LinkedList<INested<*>>()
        forEach { if (it is INested<*>) queue.add(it) }

        while (queue.isNotEmpty()) {
            val item = queue.removeLast()
            if (item is SceneObject && item !in visited) {
                visited.add(item)
                this.add(item)
                queue.addAll(item.children)
            }
        }
    }

    private val previouslyHidden = mutableSetOf<SceneObject>()
    private val previouslySelected = mutableSetOf<SceneObject>()

    private val hasNodes = objs.any { it is SceneNode }
    private val hasVoxels = objs.any { it is SceneVoxelGroup }

    override fun invoke(app: EditorAppImpl) = app.state.run {
        for (obj in objs) {
            if (obj === rootNode) continue
            // Parent node must not point to a deleted node:
            if (obj === parentNode) parentNode = rootNode
            sceneObjects.remove(obj)
            if (manuallyHiddenObjects.remove(obj)) previouslyHidden.add(obj)
            if (selectedObjects.remove(obj)) previouslySelected.add(obj)
            when (obj) {
                is SceneNode -> obj.parent?.removeChild(obj)
                is SceneVoxelGroup -> obj.parent?.removeChild(obj)
            }
        }
        app.updateHiddenObjects()
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }

    override fun revert(app: EditorAppImpl) = app.state.run {
        for (obj in objs) {
            sceneObjects.add(obj)
            if (obj in previouslySelected) selectedObjects.add(obj)
            if (obj in previouslyHidden) manuallyHiddenObjects.add(obj)
            when (obj) {
                is SceneNode -> obj.parent?.addChild(obj)
                is SceneVoxelGroup -> obj.parent?.addChild(obj)
            }
        }
        app.updateHiddenObjects()
        if (hasNodes) app.redrawNodes()
        if (hasVoxels) app.redrawVoxels()
    }
}