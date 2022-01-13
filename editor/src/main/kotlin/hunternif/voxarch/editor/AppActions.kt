package hunternif.voxarch.editor

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
    if (currentNode != rootNode) {
        scene.centerCameraAroundNode(currentNode)
    } else {
        scene.centerCameraAroundGrid()
    }
}

fun EditorApp.selectNode(node: Node) {
    currentNode = node
}

/**
 * Add child room to the currently active node.
 * [start] and [end] are in global coordinates!
 */
fun EditorApp.createRoom(start: Vector3i, end: Vector3i) {
    currentNode.findGlobalPosition()
    currentNode.run {
        val globalPos = findGlobalPosition()
        room(start.toVec3() - globalPos, end.toVec3() - globalPos)
    }
    scene.updateNodeModel()
    scene.expandEditArea(start.x, start.y, start.z)
    scene.expandEditArea(end.x, end.y, end.z)
}