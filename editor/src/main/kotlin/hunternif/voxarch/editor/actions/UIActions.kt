package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.log.LogAction
import hunternif.voxarch.editor.actions.settings.SettingsAction
import hunternif.voxarch.editor.actions.visible.HideObject
import hunternif.voxarch.editor.actions.visible.ShowAction
import hunternif.voxarch.editor.blueprint.nodeFactoryByName
import hunternif.voxarch.editor.builder.setMinecraftMaterials
import hunternif.voxarch.editor.builder.setSolidColorMaterials
import hunternif.voxarch.editor.file.VOXARCH_PROJECT_FILE_EXT
import hunternif.voxarch.editor.gui.toggle
import hunternif.voxarch.editor.scene.models.box.BoxFace
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.actions.log.LogMessage
import hunternif.voxarch.editor.util.openFileDialog
import hunternif.voxarch.editor.util.saveFileDialog

// Actions that update the state of UI and don't contribute to history

fun EditorApp.saveProjectOrOpenDialogToSaveAs() = action(UIEvent.OPEN_DIALOG) {
    if (state.projectPath != null) saveProjectFile()
    else openDialogSaveProjectFile()
}

fun EditorApp.openDialogSaveProjectFile() = action(UIEvent.OPEN_DIALOG) {
    saveFileDialog(VOXARCH_PROJECT_FILE_EXT) {
        saveProjectFileAs(it)
    }
}

fun EditorApp.openDialogOpenProjectFile() = action(UIEvent.OPEN_DIALOG) {
    openFileDialog(VOXARCH_PROJECT_FILE_EXT) {
        openProjectFile(it)
    }
}

fun EditorApp.openDialogImportVoxFile() = action(UIEvent.OPEN_DIALOG) {
    openFileDialog("vox") {
        importVoxFile(it)
        centerCamera()
    }
}

fun EditorApp.openDialogExportVoxFile() = action(UIEvent.OPEN_DIALOG) {
    saveFileDialog("vox") {
        exportVoxFile(it)
    }
}

fun EditorApp.setTool(tool: Tool) = action(UIEvent.SET_TOOL) {
    state.currentTool = tool
}

fun EditorApp.setNewNodeType(type: String) = action(UIEvent.SET_NODE_TYPE) {
    val actualType = if (type in nodeFactoryByName.keys) type else "Node"
    state.newNodeType = actualType
}

fun EditorApp.setRenderMode(mode: VoxelRenderMode) = action(UIEvent.SET_RENDER_MODE) {
    if (state.settings.renderMode != mode) {
        action(SettingsAction(state.settings.copy(renderMode = mode)))
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

fun EditorApp.setShadingMode(mode: VoxelShadingMode) = action(UIEvent.SET_SHADING_MODE) {
    action(SettingsAction(state.settings.copy(shadingMode = mode)))
    scene.updateShadingMode()
}

fun EditorApp.centerCamera() = action(UIEvent.CENTER_CAMERA) {
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

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawNodes() = action(UIEvent.REDRAW_NODES) {
    scene.updateNodeModel()
}

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawVoxels() = action(UIEvent.REDRAW_VOXELS) {
    scene.updateVoxelModel()
}

fun EditorApp.clearNewNodeFrame() = action(UIEvent.CLEAR_NEW_NODE_FRAME) {
    scene.clearNewNodeFrame()
}

/** Highlight the given face on a node. Passing null removes the highlight. */
fun EditorApp.highlightFace(face: BoxFace?) = action(UIEvent.HIGHLIGHT_FACE) {
    state.highlightedFace = face
    scene.updateHighlightedFaces()
}

fun EditorApp.focusMainWindow(focused: Boolean) = action(null) {
    state.isMainWindowFocused = focused
}

fun EditorApp.hoverMainWindow(hovered: Boolean) = action(null) {
    state.isMainWindowHovered = hovered
}

fun EditorApp.setTextEditorActive(active: Boolean) = action(null) {
    state.isTextEditorActive = active
}

fun EditorApp.logInfo(msg: String) = action(LogAction(LogMessage.info(msg)))

fun EditorApp.logWarning(msg: String) = action(LogAction(LogMessage.warn(msg)))

fun EditorApp.logError(e: Exception) = action(LogAction(LogMessage.error(e)))

fun EditorApp.addOverlayText(id: String, text: String) = action(UIEvent.SET_OVERLAY_TEXT) {
    state.overlayText[id] = text
}

fun EditorApp.removeOverlayText(id: String) = action(UIEvent.SET_OVERLAY_TEXT) {
    state.overlayText.remove(id)
}

fun EditorApp.toggleLogs() = action(UIEvent.TOGGLE_LOGS) {
    gui.showLogs.toggle()
}