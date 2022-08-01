package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.file.VOXARCH_PROJECT_FILE_EXT
import hunternif.voxarch.editor.scene.*
import hunternif.voxarch.editor.util.AABBFace
import hunternif.voxarch.editor.util.openFileDialog
import hunternif.voxarch.editor.util.saveFileDialog
import java.util.*

// Actions that update the state of UI and don't contribute to history

fun EditorApp.openDialogSaveProjectFile() = action {
    saveFileDialog(VOXARCH_PROJECT_FILE_EXT) {
        saveProjectFile(it)
    }
}

fun EditorApp.openDialogOpenProjectFile() = action {
    openFileDialog(VOXARCH_PROJECT_FILE_EXT) {
        openProjectFile(it)
    }
}

fun EditorApp.openDialogImportVoxFile() = action {
    openFileDialog("vox") {
        importVoxFile(it)
        centerCamera()
    }
}

fun EditorApp.setTool(tool: Tool) = action {
    state.currentTool = tool
}

fun EditorApp.centerCamera() = action {
    state.run {
        if (selectedObjects.isNotEmpty()) {
            // 1. look at selected objects
            scene.lookAtObjects(selectedObjects)
        } else if (parentNode != rootNode) {
            // 2. look at parent node
            scene.lookAtObjects(listOf(parentNode))
        } else {
            // 3. look at all visible objects
            val visibleList = sceneObjects.subtract(hiddenObjects)
            if (visibleList.isNotEmpty()) {
                scene.lookAtObjects(visibleList)
            } else {
                // empty scene
                scene.lookAtOrigin()
            }
        }
    }
}

fun EditorApp.showObject(obj: SceneObject) = action {
    // This object may have been hidden by one of its parents
    // To make it visible, we must un-hide all parents.
    state.manuallyHiddenObjects.remove(obj)
    when (obj) {
        is SceneNode -> {
            var parent: SceneNode? = obj
            while (parent != null) {
                state.manuallyHiddenObjects.remove(parent)
                parent = parent.parent
            }
            updateHiddenObjects()
            scene.updateNodeModel()
        }
        is SceneVoxelGroup -> {
            var parent: SceneVoxelGroup? = obj
            while (parent != null) {
                state.manuallyHiddenObjects.remove(parent)
                parent = parent.parent
            }
            updateHiddenObjects()
            scene.updateVoxelModel()
        }
    }
}

fun EditorApp.hideObject(obj: SceneObject) = action {
    state.manuallyHiddenObjects.add(obj)
    updateHiddenObjects()
    when (obj) {
        is SceneNode -> scene.updateNodeModel()
        is SceneVoxelGroup -> scene.updateVoxelModel()
    }
}

/** Repopulate state.hiddenObjects by adding children of hidden parents. */
internal fun EditorApp.updateHiddenObjects() = action {
    state.run {
        hiddenObjects.clear()
        val queue = LinkedList<INested<*>>()
        queue.addAll(manuallyHiddenObjects.filterIsInstance<INested<*>>())
        while (queue.isNotEmpty()) {
            val child = queue.removeLast()
            hiddenObjects.add(child as SceneObject)
            queue.addAll(child.children)
        }
    }
}

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawNodes() = action {
    scene.updateNodeModel()
}

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawVoxels() = action {
    scene.updateVoxelModel()
}

fun EditorApp.clearNewNodeFrame() = action {
    state.newNodeFrame.state = NewNodeFrame.State.EMPTY
    scene.updateNewNodeFrame()
}

/** Highlight the given face on a node. Passing null removes the highlight. */
fun EditorApp.highlightFace(face: AABBFace?) = action {
    state.highlightedFace = face
    scene.updateHighlightedFaces()
}

fun EditorApp.focusMainWindow(focused: Boolean) = action {
    state.isMainWindowFocused = focused
}

fun EditorApp.hoverMainWindow(hovered: Boolean) = action {
    state.isMainWindowHovered = hovered
}
