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
            menuItem("Import blueprints...") {
                app.openDialogImportProjectFile()
            }
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
            menuCheck("Spin Camera", app.state.spinCamera,
                tooltip = "Set the camera to slowly orbit around the scene, for cinematic effect"
            ) {
                app.toggleSpinCamera()
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
            menuCheck("Build", showBuild.get()) {
                showBuild.toggle()
            }
            menuCheck("Logs", showLogs.get()) {
                showLogs.toggle()
            }
        }
        menu("Build") {
            menuItem("Generate nodes",
                tooltip ="Reset generated nodes and run Blueprints for all nodes that have them."
            ) {
                app.generateNodes()
            }
            menuItem("Build voxels",
                tooltip = "Reset \"build voxels\" and build them again, from the root node."
            ) {
                app.buildVoxels()
            }
            ImGui.separator()
            menuCheck("Clean dummy nodes", app.state.cleanDummies,
                tooltip = "Automatically remove temporary \"dummy\" nodes from the generated node tree"
            ) {
                app.toggleCleanDummies()
            }
            menuCheck("Hinting", app.state.hinting,
                tooltip = "Automatically move generated node origins to the nearest global integer position"
            ) {
                app.toggleHinting()
            }
            menuCheck("Verbose DOM", app.state.verboseDom,
                tooltip = "Log verbose details when building Nodes from DOM elements (\"Generate nodes\")"
            ) {
                app.toggleVerboseDom()
            }
            menuCheck("Verbose build", app.state.verboseBuild,
                tooltip = "Log verbose details when building voxels from Nodes (\"Build voxels\")"
            ) {
                app.toggleVerboseBuild()
            }
            menuCheck("Single \"Build\" button", build.unifyButtons.get(),
                tooltip = "Use a single button to build nodes and voxels"
            ) {
                build.unifyButtons.toggle()
            }
            menuCheck("Forget build history", app.state.forgetBuildHistory,
                tooltip = "Generating Nodes or voxels doesn't write to history"
            ) {
                app.toggleForgetBuildHistory()
            }
        }
    }
}
