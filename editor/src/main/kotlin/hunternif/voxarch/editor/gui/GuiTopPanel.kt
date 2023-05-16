package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.AppState
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.clearNewNodeFrame
import hunternif.voxarch.editor.actions.createNode
import hunternif.voxarch.editor.actions.deleteSelectedObjects
import hunternif.voxarch.editor.actions.setNewNodeType
import hunternif.voxarch.editor.blueprint.NodeFactory
import hunternif.voxarch.editor.scene.NewNodeFrame.*
import imgui.ImGui

private val nodeTypeCombo = GuiCombo(
    "##new_node_type", NodeFactory.nodeTypesByName.keys, 80f)

fun MainGui.topPanel() {
    if (app.state.selectedObjects.isNotEmpty() && app.state.canDeleteSelection()) {
        button("Delete", "Delete selected objects") {
            app.deleteSelectedObjects()
        }
    }
    app.state.newNodeFrame.run {
        if (app.state.currentTool == Tool.ADD_NODE) {
            nodeTypeCombo.render(app.state.newNodeType) {
                app.setNewNodeType(it)
            }
        }
        if (state == State.COMPLETE) {
            ImGui.sameLine()
            button("Create node", "Create node from the 3d frame") {
                app.createNode(start, end, fromCenter)
                app.clearNewNodeFrame()
            }
            ImGui.sameLine()
            button("Cancel", "Remove the 3d frame for new node") {
                app.clearNewNodeFrame()
            }
        }
    }
}

private var canDeleteSelection = false
private val selectionTimer = Timer(0.1)
private fun AppState.canDeleteSelection(): Boolean {
    selectionTimer.runAtInterval {
        canDeleteSelection = selectedObjects.any { it != rootNode && it != voxelRoot }
    }
    return canDeleteSelection
}
