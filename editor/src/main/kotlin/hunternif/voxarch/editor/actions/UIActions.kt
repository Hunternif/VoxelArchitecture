package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.gui.Colors
import hunternif.voxarch.editor.scene.NewNodeFrame
import hunternif.voxarch.editor.scene.models.BoxInstancedModel.InstanceData
import hunternif.voxarch.editor.scene.models.NodeModel
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.Node
import org.joml.Vector3f

// Actions that update the state of UI and don't contribute to history

fun EditorApp.setTool(tool: Tool) = action {
    state.currentTool = tool
}

fun EditorApp.centerCamera() = action {
    state.run {
        if (selectedNodes.isNotEmpty()) {
            // 1. look at selected nodes
            scene.lookAtNodes(selectedNodes)
        } else if (parentNode != rootNode) {
            // 2. look at parent node
            scene.lookAtNodes(listOf(parentNode))
        } else {
            // 3. look at all visible nodes
            val visibleNodes = mutableListOf<InstanceData>()
            visibleNodes.addAll(
                nodeDataMap.keys.subtract(hiddenNodes).map { nodeData(it) }
            )
            if (voxels.isNotEmpty()) {
                // pseudo-node instance that contains voxel data
                visibleNodes.add(
                    voxels.run {
                        InstanceData(
                            Vector3f(minX - 0.5f, minY - 0.5f, minZ - 0.5f),
                            Vector3f(maxX + 0.5f, maxY + 0.5f, maxZ + 0.5f),
                            ColorRGBa.fromHex(0, 0f)
                        )
                    }
                )
            }
            if (visibleNodes.isNotEmpty()) {
                scene.lookAtBoxes(visibleNodes)
            } else {
                // empty scene
                scene.lookAtOrigin()
            }
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