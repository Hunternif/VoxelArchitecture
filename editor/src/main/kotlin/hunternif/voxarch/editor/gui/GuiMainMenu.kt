package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode.*
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode.*
import imgui.ImGui

fun MainGui.mainMenu() {
    mainMenuBar {
        menu("File") {
            menuItem("New", "Ctrl+N") {
                app.newProject()
            }
            menuItem("Open...", "Ctrl+O") {
                app.openDialogOpenProjectFile()
            }
            menuItem("Save", "Ctrl+S", app.state.projectPath != null) {
                app.saveProjectFile()
            }
            menuItem("Save as...", "Ctrl+Shift+S") {
                app.openDialogSaveProjectFile()
            }
            ImGui.separator()
            menuItem("Import VOX...", "Ctrl+I") {
                app.openDialogImportVoxFile()
            }
            menuItem("Export VOX...") {
                app.openDialogExportVoxFile()
            }
        }
        menu("Edit") {
            menuItem("Undo", "Ctrl+Z", app.state.history.hasPastItems()) {
                app.undo()
            }
            menuItem("Redo", "Ctrl+Shift+Z", app.state.history.hasFutureItems()) {
                app.redo()
            }
        }
        menu("View") {
            menu("Render mode") {
                menuCheck("Solid color", app.state.renderMode == COLORED) {
                    app.setRenderMode(COLORED)
                }
                menuCheck("Texture", app.state.renderMode == TEXTURED) {
                    app.setRenderMode(TEXTURED)
                }
                ImGui.separator()
                menuCheck("MagicaVoxel", app.state.shadingMode == MAGICA_VOXEL) {
                    app.setShadingMode(MAGICA_VOXEL)
                }
                menuCheck("Minecraft", app.state.shadingMode == MINECRAFT) {
                    app.setShadingMode(MINECRAFT)
                }
            }
            ImGui.separator()
            menuCheck("Style Editor", showStyleEditor.get()) {
                showStyleEditor.toggle()
            }
            menuCheck("Blueprint Library", showBlueprintLibrary.get()) {
                showBlueprintLibrary.toggle()
            }
            menuCheck("Blueprint Editor", showBlueprintEditor.get()) {
                showBlueprintEditor.toggle()
            }
            menuCheck("Node tree", showNodeTree.get()) {
                showNodeTree.toggle()
            }
            menuCheck("Voxel tree", showVoxelTree.get()) {
                showVoxelTree.toggle()
            }
            menuCheck("History", showHistory.get()) {
                showHistory.toggle()
            }
            menuCheck("Properties", showProperties.get()) {
                showProperties.toggle()
            }
            menuCheck("Logs", showLogs.get()) {
                showLogs.toggle()
            }
        }
    }
}
