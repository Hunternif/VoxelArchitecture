package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.SelectMask.*
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


//=============================== VOXELS ================================

fun EditorApp.importVoxFile(path: Path) = historyAction(ImportVoxFile(path))


//============================== SELECTION ==============================

/** Modify selection in multiple steps. */
fun EditorApp.selectionBuilder(mask: SelectMask = ALL) = action {
    SelectObjectsBuilder(this, mask)
}

/** Add the given object to selection. */
fun EditorApp.selectObject(obj: SceneObject) = selectionBuilder().run {
    add(obj)
    commit()
}

/** Clear current selection and select only the given object.
 * Selected objects not filtered by [mask] will stay selected. */
fun EditorApp.setSelectedObject(obj: SceneObject, mask: SelectMask = ALL) =
    selectionBuilder(mask).run {
        clear()
        add(obj)
        commit()
    }

/** Remove the given object from selection. */
fun EditorApp.unselectObject(obj: SceneObject) = selectionBuilder().run {
    remove(obj)
    commit()
}

/** Select all objects filtered by [mask]. */
fun EditorApp.selectAll(mask: SelectMask = ALL) =
    selectionBuilder(mask).run {
        state.sceneObjects.forEach { add(it) }
        commit()
    }

/** Unselect all objects filtered by [mask]. */
fun EditorApp.unselectAll(mask: SelectMask = ALL) =
    selectionBuilder(mask).run {
        clear()
        commit()
    }

fun EditorApp.setParentNode(node: SceneNode) = historyAction(SetParent(node))


//============================== TRANSFORM ==============================

/** Modify object position in multiple steps. */
fun EditorApp.moveBuilder(objs: Collection<SceneObject>) = action {
    MoveObjectsBuilder(this, objs.toList())
}

/** Modify object size in multiple steps.
 * Room start will be modified to keep origin in the same global position.
 * A centered room will be resized symmetrically. */
fun EditorApp.resizeBuilder(objs: Collection<SceneObject>) = action {
    ResizeNodesBuilder(this, objs.toList())
}

fun EditorApp.transformNodeOrigin(
    obj: SceneNode,
    oldOrigin: Vec3,
    newOrigin: Vec3,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(origin = oldOrigin)),
        mapOf(obj to obj.transformData(origin = newOrigin)),
        "Transform node (origin)",
    )
)

fun EditorApp.transformNodeSize(
    obj: SceneNode,
    oldSize: Vec3,
    newSize: Vec3,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(size = oldSize)),
        mapOf(obj to obj.transformData(size = newSize)),
        "Transform node (size)",
    )
)

fun EditorApp.transformNodeStart(
    obj: SceneNode,
    oldStart: Vec3,
    newStart: Vec3,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(start = oldStart)),
        mapOf(obj to obj.transformData(start = newStart)),
        "Transform node (start)",
    )
)

/** Can potentially modify origin and start, to preserve global node position. */
fun EditorApp.transformNodeCentered(
    obj: SceneNode,
    newCentered: Boolean,
) {
    val newOrigin = (obj.node as? Room)?.run {
        if (newCentered) origin + start + Vec3(size.x/2, 0.0, size.z/2)
        else origin + start
    }
    val newStart = if (obj.node is Room && !newCentered) Vec3.ZERO else null
    historyAction(
        TransformObjects(
            mapOf(obj to obj.transformData(isCentered = !newCentered)),
            mapOf(obj to obj.transformData(isCentered = newCentered,
                origin = newOrigin, start = newStart)),
            "Transform node (${if (newCentered) "center" else "uncenter"})",
        )
    )
}


/**
 * Add child room to the currently active node.
 * [start] and [end] are in global 'centric' coordinates!
 */
fun EditorApp.createRoom(
    start: Vector3i, end: Vector3i, centered: Boolean = false
) : SceneNode = action {
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
        scene.updateNodeModel()
        sceneNode
    }
}

fun EditorApp.deleteSelectedObjects() = action {
    deleteObjects(state.selectedObjects)
}

fun EditorApp.deleteObjects(objs: Collection<SceneObject>) = action {
    var removedNode = false
    var removedVoxels = false
    state.run {
        // copy the list in case e.g. we were iterating over [selectedObjects]
        for (obj in objs.toList()) {
            if (obj == rootNode) continue
            sceneObjects.remove(obj)
            hiddenObjects.remove(obj)
            selectedObjects.remove(obj)
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
}

fun EditorApp.undo() = action {
    state.history.moveBack()?.revert(this)
}

fun EditorApp.redo() = action {
    state.history.moveForward()?.invoke(this)
}


/////////////////////////// TECHNICAL ACTIONS ///////////////////////////////

/** Simple action that isn't written to history. */
internal inline fun <T> EditorApp.action(
    crossinline execute: EditorAppImpl.() -> T
): T {
    return (this as EditorAppImpl).execute()
}

internal fun EditorApp.historyAction(action: HistoryAction): Unit = action {
    action.invoke(this)
    state.history.append(action)
}
