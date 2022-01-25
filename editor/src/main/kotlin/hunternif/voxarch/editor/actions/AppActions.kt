package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i

// This contains all actions that can be performed via UI.
// Some of them can support keyboard shortcuts, console commands, undo/redo.

// EditorApp must be injected into all classes that call these actions.

/** Add the given node to selection. */
fun EditorApp.selectNode(node: Node) = action {
    if (state.selectedNodes.add(node)) {
        scene.updateSelectedNodeModel()
    }
}

/** Remove the given node from selection. */
fun EditorApp.unselectNode(node: Node) = action {
    if (state.selectedNodes.remove(node)) {
        scene.updateSelectedNodeModel()
    }
}

/** Select a single node */
fun EditorApp.setSelectedNode(node: Node?) = action {
    state.run {
        selectedNodes.clear()
        if (node != null && node != rootNode) selectedNodes.add(node)
    }
    scene.updateSelectedNodeModel()
}

fun EditorApp.setParentNode(node: Node) = action {
    state.parentNode = node
}

fun EditorApp.showNode(node: Node) = action {
    // This node may have been hidden by one of its parents
    // To make it visible, we must un-hide all parents.
    var parent: Node? = node
    while (parent != null) {
        state.hiddenNodes.remove(parent)
        parent = parent.parent
    }
    scene.updateNodeModel()
}

fun EditorApp.hideNode(node: Node) = action {
    state.hiddenNodes.add(node)
    scene.updateNodeModel()
}

/** The given [node] is assumed to already exist in the scene, and to contain
 * the updated data. In the future the update might need to happen here... */
fun EditorApp.updateNode(node: Node) = action {
    scene.updateNodeModel()
}

/** Modify Node.isCentered() to new value. This moves origin so that node's
 * global position stays the same. */
fun EditorApp.modifyNodeCentered(node: Node, centered: Boolean) = action {
    (node as? Room)?.apply {
        if (isCentered() == centered) return@action
        if (centered) {
            origin += start + Vec3(size.x/2, 0.0, size.z/ 2)
            setCentered(true)
        } else {
            origin += start
            setCentered(false)
            start.set(0, 0, 0)
        }
        scene.updateNodeModel()
    }
}

/**
 * Add child room to the currently active node.
 * [start] and [end] are in global coordinates!
 */
fun EditorApp.createRoom(
    start: Vector3i, end: Vector3i, centered: Boolean = false
) = action {
    // ensure size is positive
    val min = min(start, end).toVec3()
    val max = max(start, end).toVec3()
    val size = max - min
    val mid = min.add(size.x / 2 , 0.0, size.z / 2)
    state.run {
        parentNode.run {
            val globalPos = findGlobalPosition()
            if (centered) {
                centeredRoom(mid - globalPos, size)
            } else {
                room(min - globalPos, max - globalPos)
            }
        }
        workArea.union(min.x - gridMargin, min.y, min.z - gridMargin)
        workArea.union(max.x + gridMargin, max.y, max.z + gridMargin)
    }
    scene.updateGrid()
    scene.updateNodeModel()
}

fun EditorApp.deleteSelectedNodes() = action {
    state.run {
        for (node in selectedNodes) {
            if (node == rootNode) continue
            hiddenNodes.remove(node)
            node.parent?.removeChild(node)
            nodeDataMap.remove(node)
        }
        selectedNodes.clear()
    }
    scene.updateNodeModel()
}

fun EditorApp.deleteNode(node: Node) = action {
    state.run {
        if (node == rootNode) return@action
        selectedNodes.remove(node)
        hiddenNodes.remove(node)
        nodeDataMap.remove(node)
    }
    node.parent?.removeChild(node)
    scene.updateNodeModel()
}


/////////////////////////// TECHNICAL ACTIONS ///////////////////////////////

fun <T> EditorApp.action(execute: EditorAppImpl.() -> T): T {
    return (this as EditorAppImpl).execute()
}
