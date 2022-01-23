package hunternif.voxarch.editor

import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
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

/** Add the given node to selection. */
fun EditorApp.selectNode(node: Node) {
    if (selectedNodes.add(node)) {
        scene.updateSelectedNodeModel()
    }
}

/** Remove the given node from selection. */
fun EditorApp.unselectNode(node: Node) {
    if (selectedNodes.remove(node)) {
        scene.updateSelectedNodeModel()
    }
}

/** Select a single node */
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

/** Modify Node.isCentered() to new value. This moves origin so that node's
 * global position stays the same. */
fun EditorApp.modifyNodeCentered(node: Node, centered: Boolean) {
    (node as? Room)?.apply {
        if (isCentered() == centered) return
        if (centered) {
            origin += start + Vec3(size.x/2, 0.0, size.z/ 2)
            setCentered(true)
        } else {
            origin += start
            setCentered(false)
            start.set(0, 0, 0)
        }
    }
    scene.updateNodeModel()
}

/** Used by UI to show real-time updates that aren't yet written to history. */
fun EditorApp.redrawNodes() {
    scene.updateNodeModel()
}

/**
 * Add child room to the currently active node.
 * [start] and [end] are in global coordinates!
 */
fun EditorApp.createRoom(start: Vector3i, end: Vector3i, centered: Boolean = false) {
    // ensure size is positive
    val min = min(start, end).toVec3()
    val max = max(start, end).toVec3()
    val size = max - min
    val mid = min.add(size.x / 2 , 0.0, size.z / 2)
    parentNode.findGlobalPosition()
    parentNode.run {
        val globalPos = findGlobalPosition()
        if (centered) {
            centeredRoom(mid - globalPos, size)
        } else {
            room(min - globalPos, max - globalPos)
        }
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