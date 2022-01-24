package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.models.NodeModel
import hunternif.voxarch.plan.Node
import org.joml.Vector3f

// Actions that update the state of UI and don't contribute to history

fun EditorApp.setTool(tool: Tool) = action {
    state.currentTool = tool
}

fun EditorApp.centerCamera() = action {
    state.run {
        if (parentNode == rootNode) {
            scene.centerCameraAroundGrid()
        } else {
            scene.centerCameraAroundNode(parentNode)
        }
    }
}

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawNodes() = action {
    scene.updateNodeModel()
}

fun EditorApp.clearNewNodeFrame() = action {
    state.newNodeFrame.state = NewNodeFrame.State.EMPTY
    scene.updateNewNodeFrame()
}

fun EditorApp.focusMainWindow(focused: Boolean) = action {
    state.isMainWindowFocused = focused
}

/** Returns node instance data, i.e. render-related data. */
fun EditorApp.nodeData(node: Node): NodeModel.NodeData = action {
    state.nodeDataMap.getOrPut(node) {
        NodeModel.NodeData(Vector3f(), Vector3f(), Colors.defaultNodeBox, node)
    }
}