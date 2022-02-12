package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.AABBFace
import imgui.ImGui
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog.*
import java.lang.Exception
import java.nio.file.Paths

// Actions that update the state of UI and don't contribute to history

//TODO: use a separate thread
fun EditorApp.openDialogImportVoxFile() = action {
    val outPath = MemoryUtil.memAllocPointer(1)
    try {
        if (NFD_OKAY == NFD_OpenDialog("vox", null, outPath)) {
            val pathStr = outPath.getStringUTF8(0)
            val path = Paths.get(pathStr)
            importVoxFile(path)
            centerCamera()
        }
    } catch (e: Exception) {
        ImGui.text(e.toString())
    } finally {
        MemoryUtil.memFree(outPath)
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
    state.hiddenObjects.remove(obj)
    when (obj) {
        is SceneNode -> {
            var parent: SceneNode? = obj
            while (parent != null) {
                state.hiddenObjects.remove(parent)
                parent = parent.parent
            }
            scene.updateNodeModel()
        }
        is SceneVoxelGroup -> {
            var parent: SceneVoxelGroup? = obj
            while (parent != null) {
                state.hiddenObjects.remove(parent)
                parent = parent.parent
            }
            scene.updateVoxelModel()
        }
    }
}

fun EditorApp.hideObject(obj: SceneObject) = action {
    state.hiddenObjects.add(obj)
    when (obj) {
        is SceneNode -> scene.updateNodeModel()
        is SceneVoxelGroup -> scene.updateVoxelModel()
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
