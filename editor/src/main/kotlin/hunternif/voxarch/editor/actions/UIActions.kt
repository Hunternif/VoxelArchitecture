package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.visible.HideObject
import hunternif.voxarch.editor.actions.visible.ShowAction
import hunternif.voxarch.editor.blueprint.NodeFactory
import hunternif.voxarch.editor.builder.setMinecraftMaterials
import hunternif.voxarch.editor.builder.setSolidColorMaterials
import hunternif.voxarch.editor.file.VOXARCH_PROJECT_FILE_EXT
import hunternif.voxarch.editor.gui.toggle
import hunternif.voxarch.editor.scene.models.box.BoxFace
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.editor.util.LogMessage
import hunternif.voxarch.editor.util.openFileDialog
import hunternif.voxarch.editor.util.saveFileDialog

// Actions that update the state of UI and don't contribute to history

fun EditorApp.saveProjectOrOpenDialogToSaveAs() = action {
    if (state.projectPath != null) saveProjectFile()
    else openDialogSaveProjectFile()
}

fun EditorApp.openDialogSaveProjectFile() = action {
    saveFileDialog(VOXARCH_PROJECT_FILE_EXT) {
        saveProjectFileAs(it)
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

fun EditorApp.openDialogExportVoxFile() = action {
    saveFileDialog("vox") {
        exportVoxFile(it)
    }
}

fun EditorApp.setTool(tool: Tool) = action {
    state.currentTool = tool
}

fun EditorApp.setNewNodeType(type: String) = action {
    val actualType = if (type in NodeFactory.nodeTypesByName.keys) type else "Node"
    state.newNodeType = actualType
}

fun EditorApp.setRenderMode(mode: VoxelRenderMode) = action {
    if (state.renderMode != mode) {
        state.renderMode = mode
        when (mode) {
            VoxelRenderMode.COLORED -> {
                state.buildContext.materials.setSolidColorMaterials()
            }
            VoxelRenderMode.TEXTURED -> {
                state.buildContext.materials.setMinecraftMaterials()
            }
        }
        if (state.generatedVoxels.isNotEmpty()) buildVoxels()
    }
}

fun EditorApp.setShadingMode(mode: VoxelShadingMode) = action {
    state.shadingMode = mode
    scene.updateShadingMode()
}

fun EditorApp.centerCamera() = action {
    state.run {
        val selectedObjectsExceptRoot = selectedObjects - rootNode
        if (selectedObjectsExceptRoot.isNotEmpty()) {
            // 1. look at selected objects
            scene.lookAtObjects(selectedObjectsExceptRoot)
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

fun EditorApp.showObject(obj: SceneObject) = action(ShowAction(obj))

fun EditorApp.hideObject(obj: SceneObject) = action(HideObject(obj))

fun EditorApp.scrollNodeTreeTo(obj: SceneObject) = action {
    when (obj) {
        is SceneNode -> gui.nodeTree.scrollToItem(obj)
        is SceneVoxelGroup -> gui.voxelTree.scrollToItem(obj)
    }
}

/** This can be called every frame, to mark the object as hovered */
fun EditorApp.hoverObject(obj: SceneObject) = action {
    gui.hoverController.hover(obj)
}

/** This can be called every frame, to update the scene with new hovered objects. */
fun EditorApp.setHoveredObjects(objs: Set<SceneObject>) = action {
    if (state.hoveredObjects != objs) {
        state.hoveredObjects.clear()
        state.hoveredObjects.addAll(objs)
        scene.updateHoveredModel(objs)
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
    scene.clearNewNodeFrame()
}

/** Highlight the given face on a node. Passing null removes the highlight. */
fun EditorApp.highlightFace(face: BoxFace?) = action {
    state.highlightedFace = face
    scene.updateHighlightedFaces()
}

fun EditorApp.focusMainWindow(focused: Boolean) = action {
    state.isMainWindowFocused = focused
}

fun EditorApp.hoverMainWindow(hovered: Boolean) = action {
    state.isMainWindowHovered = hovered
}

fun EditorApp.setTextEditorActive(active: Boolean) = action {
    state.isTextEditorActive = active
}

fun EditorApp.logInfo(msg: String) = action {
    gui.log.append(LogMessage.info(msg))
}

fun EditorApp.logWarning(msg: String) = action {
    gui.log.append(LogMessage.warn(msg))
}

fun EditorApp.logError(e: Exception) = action {
    gui.log.append(LogMessage.error(e))
}

fun EditorApp.logError(msg: String) = action {
    gui.log.append(LogMessage.error(msg))
}

fun EditorApp.addOverlayText(id: String, text: String) = action {
    state.overlayText[id] = text
}

fun EditorApp.removeOverlayText(id: String) = action {
    state.overlayText.remove(id)
}

fun EditorApp.toggleLogs() = action {
    gui.showLogs.toggle()
}