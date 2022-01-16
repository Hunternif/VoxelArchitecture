package hunternif.voxarch.editor

import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.Node
import hunternif.voxarch.plan.findGlobalPosition
import hunternif.voxarch.plan.room
import org.joml.Vector3i

// This contains all actions that can be performed via UI.
// Some of them can support keyboard shortcuts, console commands, undo/redo.
// In order to support history, function arguments should be primitive values,
// i.e. not object references.
// TODO: decide how to reference nodes

// EditorApp must be injected into all classes that call these actions.

fun EditorApp.centerCamera() {
    if (parentNode != rootNode) {
        scene.centerCameraAroundNode(parentNode)
    } else {
        scene.centerCameraAroundGrid()
    }
}

fun EditorApp.setTool(tool: Tool) {
    currentTool = tool
}

fun EditorApp.setSelectedNode(node: Node?) {
    selectedNodes.clear()
    if (node != null && node != rootNode) selectedNodes.add(node)
    scene.updateSelectedNodeModel()
}

fun EditorApp.setParentNode(node: Node) {
    parentNode = node
}

fun EditorApp.showNode(node: Node) {
    // This node may have been hidden by one of its parents
    // To make it visible, we must un-hide all parents.
    var parent: Node? = node
    while (parent != null) {
        hiddenNodes.remove(parent)
        parent = parent.parent
    }
    scene.updateNodeModel()
}

fun EditorApp.hideNode(node: Node) {
    hiddenNodes.add(node)
    scene.updateNodeModel()
}

/** The given [node] is assumed to already exist in the scene, and to contain
 * the updated data. In the future the update might need to happen here... */
fun EditorApp.updateNode(node: Node) {
    scene.updateNodeModel()
}

/**
 * Add child room to the currently active node.
 * [start] and [end] are in global coordinates!
 */
fun EditorApp.createRoom(start: Vector3i, end: Vector3i) {
    // ensure size is positive
    val min = min(start, end)
    val max = max(start, end)
    parentNode.findGlobalPosition()
    parentNode.run {
        val globalPos = findGlobalPosition()
        room(min.toVec3() - globalPos, max.toVec3() - globalPos)
    }
    scene.updateNodeModel()
    scene.expandEditArea(start.x, start.y, start.z)
    scene.expandEditArea(end.x, end.y, end.z)
}

fun EditorApp.deleteSelectedNodes() {
    for (node in selectedNodes) {
        if (node == rootNode) continue
        hiddenNodes.remove(node)
        node.parent?.removeChild(node)
    }
    selectedNodes.clear()
    scene.updateNodeModel()
}

fun EditorApp.deleteNode(node: Node) {
    if (node == rootNode) return
    selectedNodes.remove(node)
    hiddenNodes.remove(node)
    node.parent?.removeChild(node)
    scene.updateNodeModel()
}