package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.editor.scene.SceneVoxelGroup
import hunternif.voxarch.editor.util.max
import hunternif.voxarch.editor.util.min
import hunternif.voxarch.editor.util.toVec3
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import java.nio.file.Path

// This contains all actions that can be performed via UI.
// Some of them can support keyboard shortcuts, console commands, undo/redo.

// EditorApp must be injected into all classes that call these actions.

fun EditorApp.importVoxFile(path: Path) = historyAction(ImportVoxFile(path))

/** Add the given object to selection. */
fun EditorApp.selectObject(obj: SceneObject) = action {
    if (state.selectedObjects.add(obj)) {
        scene.updateSelectedNodeModel()
    }
}

/** Remove the given object from selection. */
fun EditorApp.unselectObject(obj: SceneObject) = action {
    if (state.selectedObjects.remove(obj)) {
        scene.updateSelectedNodeModel()
    }
}

/** Select a single object */
fun EditorApp.setSelectedObject(node: SceneObject?) = action {
    state.run {
        selectedObjects.clear()
        if (node != null && node != rootNode) selectedObjects.add(node)
    }
    scene.updateSelectedNodeModel()
}

fun EditorApp.setParentNode(node: SceneNode) = action {
    state.parentNode = node
}

fun EditorApp.showObject(obj: SceneObject) = action {
    // This object may have been hidden by one of its parents
    // To make it visible, we must un-hide all parents.
    state.hiddenObjects.remove(obj)
    when (obj) {
        is SceneNode -> {
            var parent: SceneNode? = obj
            while (parent != null) {
                state.hiddenObjects.remove(parent)
                parent = parent.parent
            }
            scene.updateNodeModel()
        }
        is SceneVoxelGroup -> {
            var parent: SceneVoxelGroup? = obj
            while (parent != null) {
                state.hiddenObjects.remove(parent)
                parent = parent.parent
            }
            scene.updateVoxelModel()
        }
    }
}

fun EditorApp.hideObject(obj: SceneObject) = action {
    state.hiddenObjects.add(obj)
    when (obj) {
        is SceneNode -> scene.updateNodeModel()
        is SceneVoxelGroup -> scene.updateVoxelModel()
    }
}

/** The given [obj] is assumed to already exist in the scene, and to contain
 * the updated data. In the future the update might need to happen here... */
fun EditorApp.updateObject(obj: SceneObject) = action {
    when (obj) {
        is SceneNode -> scene.updateNodeModel()
        is SceneVoxelGroup -> scene.updateVoxelModel()
    }
    scene.updateSelectedNodeModel()
}

/** Modify Node.isCentered() to new value. This moves origin so that node's
 * global position stays the same. */
fun EditorApp.modifyNodeCentered(node: SceneNode, centered: Boolean) = action {
    (node.node as? Room)?.apply {
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
        val room = parentNode.node.run {
            val globalPos = findGlobalPosition()
            if (centered) {
                centeredRoom(mid - globalPos, size)
            } else {
                room(min - globalPos, max - globalPos)
            }
        }
        val sceneNode = SceneNode(room)
        sceneObjects.add(sceneNode)
        parentNode.addChild(sceneNode)
    }
    scene.updateNodeModel()
}

fun EditorApp.deleteSelectedObjects() = action {
    deleteObjects(state.selectedObjects)
    state.selectedObjects.clear()
    scene.updateSelectedNodeModel()
}

fun EditorApp.deleteObjects(objs: Collection<SceneObject>) = action {
    var removedNode = false
    var removedVoxels = false
    state.run {
        for (obj in objs) {
            if (obj == rootNode) continue
            sceneObjects.remove(obj)
            hiddenObjects.remove(obj)
            when (obj) {
                is SceneNode -> {
                    obj.parent?.removeChild(obj)
                    removedNode = true
                }
                is SceneVoxelGroup -> {
                    obj.parent?.removeChild(obj)
                    removedVoxels = true
                }
            }
        }
    }
    if (removedNode) scene.updateNodeModel()
    if (removedVoxels) scene.updateVoxelModel()
    scene.updateSelectedNodeModel()
}

fun EditorApp.undo() = action {
    state.history.moveBack()?.revert(this)
}

fun EditorApp.redo() = action {
    state.history.moveForward()?.invoke(this)
}


/////////////////////////// TECHNICAL ACTIONS ///////////////////////////////

/** Simple action that isn't written to history. */
internal fun <T> EditorApp.action(execute: EditorAppImpl.() -> T): T {
    return (this as EditorAppImpl).execute()
}

internal fun EditorApp.historyAction(action: HistoryAction): Unit = action {
    action.invoke(this)
    state.history.append(action)
}

abstract class HistoryAction(val description: String) {
    abstract fun invoke(app: EditorAppImpl)
    abstract fun revert(app: EditorAppImpl)
}