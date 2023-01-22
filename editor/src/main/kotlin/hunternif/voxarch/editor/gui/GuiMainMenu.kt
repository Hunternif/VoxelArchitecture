package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.shaders.VoxelRenderMode.*
import hunternif.voxarch.editor.scene.shaders.VoxelShadingMode.*
import imgui.ImGui
import imgui.flag.ImGuiStyleVar

fun MainGui.mainMenu() {
    ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0f)
    if (ImGui.beginMainMenuBar()) {
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("New", "Ctrl+N")) {
                app.newProject()
            }
            if (ImGui.menuItem("Open...", "Ctrl+O")) {
                app.openDialogOpenProjectFile()
            }
            disabled(app.state.projectPath == null) {
                if (ImGui.menuItem("Save", "Ctrl+S")) {
                    app.saveProjectFile()
                }
            }
            if (ImGui.menuItem("Save as...", "Ctrl+Shift+S")) {
                app.openDialogSaveProjectFile()
            }
            ImGui.separator()
            if (ImGui.menuItem("Import VOX...", "Ctrl+I")) {
                app.openDialogImportVoxFile()
            }
            if (ImGui.menuItem("Export VOX...")) {
                app.openDialogExportVoxFile()
            }
            ImGui.endMenu()
        }
        if (ImGui.beginMenu("Edit")) {
            if (ImGui.menuItem(
                    "Undo",
                    "Ctrl+Z",
                    false,
                    app.state.history.hasPastItems()
                )) app.undo()
            if (ImGui.menuItem(
                    "Redo",
                    "Ctrl+Shift+Z",
                    false,
                    app.state.history.hasFutureItems()
                )) app.redo()
            ImGui.endMenu()
        }
        if (ImGui.beginMenu("Options")) {
            if (ImGui.beginMenu("Render mode")) {
                if (ImGui.menuItem("Solid color", "",
                        app.state.renderMode == COLORED
                    )) app.setRenderMode(COLORED)
                if (ImGui.menuItem("Texture", "",
                        app.state.renderMode == TEXTURED
                    )) app.setRenderMode(TEXTURED)
                ImGui.separator()
                if (ImGui.menuItem("MagicaVoxel", "",
                        app.state.shadingMode == MAGICA_VOXEL
                    )) app.setShadingMode(MAGICA_VOXEL)
                if (ImGui.menuItem("Minecraft", "",
                        app.state.shadingMode == MINECRAFT
                    )) app.setShadingMode(MINECRAFT)
                ImGui.endMenu()
            }
            ImGui.endMenu()
        }
        ImGui.endMainMenuBar()
    }
    ImGui.popStyleVar(1)
}
