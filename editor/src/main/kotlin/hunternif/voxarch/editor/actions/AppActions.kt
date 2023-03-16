package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.SelectMask.*
import hunternif.voxarch.editor.file.writeProject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.scenegraph.SceneVoxelGroup
import hunternif.voxarch.magicavoxel.VoxColor
import hunternif.voxarch.magicavoxel.writeToVoxFile
import hunternif.voxarch.plan.naturalToCentric
import hunternif.voxarch.storage.ChunkedStorage3D
import hunternif.voxarch.storage.IVoxel
import hunternif.voxarch.util.SnapOrigin
import hunternif.voxarch.util.copyTo
import hunternif.voxarch.util.forEachSubtree
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import java.nio.file.Path

// This contains all "important" actions that modify App state,
// contribute to history and can be performed via UI.
// (As an exception, actions on project files may ignore history.)
// Some of them can support keyboard shortcuts, console commands, undo/redo.

// EditorApp must be injected into all classes that call these actions.


//============================ PROJECT FILE =============================

fun EditorApp.newProject() = historyAction(NewProject())

fun EditorApp.openProjectFile(path: Path) = historyAction(OpenProject(path))

fun EditorApp.saveProjectFile() = action {
    state.projectPath?.let { writeProject(it) }
    state.lastSavedAction = state.history.pastItems.last
}

fun EditorApp.saveProjectFileAs(path: Path) = action {
    writeProject(path)
    state.projectPath = path
    state.lastSavedAction = state.history.pastItems.last
}

fun EditorApp.exportVoxFile(path: Path) = action {
    // Copy all vox groups into a single storage
    //TODO: serialize separate VOX groups
    val mergedStorage = ChunkedStorage3D<IVoxel>()
    state.voxelRoot.forEachSubtree {
        if (it is SceneVoxelGroup) {
            it.data.copyTo(mergedStorage, it.findGlobalPosition().toIntVec3())
        }
    }
    mergedStorage.writeToVoxFile(path) { v ->
        when (v) {
            is VoxColor -> v
            else -> VoxColor(state.voxelColorMap(v).hex)
        }
    }
    logWarning("Export complete to '$path'")
}


//=============================== VOXELS ================================

fun EditorApp.importVoxFile(path: Path) = historyAction(ImportVoxFile(path))

/** Clear generated nodes and run Blueprints for all nodes that have them. */
fun EditorApp.generateNodes() = historyAction(GenerateNodes())

/** Clear generated voxels and run a Builder on the current root node. */
fun EditorApp.buildVoxels() = historyAction(BuildVoxels())


//============================== SELECTION ==============================

/** Modify selection in multiple steps. */
fun EditorApp.selectionBuilder(mask: SelectMask = ALL) =
    SelectObjectsBuilder(this as EditorAppImpl, mask)

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
fun EditorApp.moveBuilder(objs: Collection<SceneObject>) =
    MoveObjectsBuilder(this as EditorAppImpl, objs.toList())

/** Modify object size in multiple steps.
 * Node start will be modified to keep origin in the same global position.
 * A centered room will be resized symmetrically. */
fun EditorApp.resizeBuilder(objs: Collection<SceneObject>) =
    ResizeNodesBuilder(this as EditorAppImpl, objs.toList())

/** Passing in [oldOrigin] because the node could be already moved in the scene. */
fun EditorApp.transformObjOrigin(
    obj: SceneObject,
    oldOrigin: Vec3,
    newOrigin: Vec3,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(origin = oldOrigin)),
        mapOf(obj to obj.transformData(origin = newOrigin)),
        "Transform node (origin)",
    )
)

/** Passing in [oldSize] because the node could be already resized in the scene.
 * Uses "natural" coordinates. */
fun EditorApp.transformNodeNaturalSize(
    obj: SceneNode,
    oldSize: Vec3,
    newSize: Vec3,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(size = oldSize.naturalToCentric())),
        mapOf(obj to obj.transformData(size = newSize.naturalToCentric())),
        "Transform node (size)",
    )
)

/** Passing in [oldStart] because the node could be already moved in the scene. */
fun EditorApp.transformNodeStart(
    obj: SceneNode,
    oldStart: Vec3,
    newStart: Vec3,
) {
    // Changing snap origin potentially moves origins of children:
    val oldData = obj.children.associateWithTo(mutableMapOf()) { it.transformData() }
    oldData[obj] = obj.transformData(start = oldStart)
    historyAction(
        TransformObjects(
            oldData,
            mapOf(obj to obj.transformData(start = newStart, snapOrigin = SnapOrigin.OFF)),
            "Transform node (start)",
        )
    )
}

/** Passing in [oldRotation] because the node could be already rotated in the scene. */
fun EditorApp.transformNodeRotation(
    obj: SceneNode,
    oldRotation: Double,
    newRotation: Double,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(rotationY = oldRotation)),
        mapOf(obj to obj.transformData(rotationY = newRotation)),
        "Transform node (rotation)",
    )
)

/** Snap node origin to one of the standard places */
fun EditorApp.transformNodeSnapOrigin(
    obj: SceneNode,
    snapOrigin: SnapOrigin,
) {
    // This potentially moves origins of children:
    val oldData = (obj.children + obj).associateWith { it.transformData() }
    obj.snapOrigin = snapOrigin
    val newData = (obj.children + obj).associateWith { it.transformData() }
    historyAction(
        TransformObjects(oldData, newData,
            "Transform node (snap origin)",
        )
    )
}


//=========================== CREATE & DELETE ===========================

/**
 * Add child node to the currently active node.
 * [start] and [end] are in global 'centric' coordinates!
 */
fun EditorApp.createNode(
    start: Vector3i, end: Vector3i,
    centered: Boolean = false,
    type: String = "Node",
): SceneNode {
    val action = CreateNode(start, end, centered, type)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteSelectedObjects() = action {
    deleteObjects(state.selectedObjects)
}

fun EditorApp.deleteObjects(objs: Collection<SceneObject>) {
    objs.filter { it != state.rootNode && it != state.voxelRoot }.let {
        if (it.isNotEmpty()) historyAction(DeleteObjects(it))
    }
}


//=============================== HISTORY ===============================

fun EditorApp.undo() = action {
    state.history.moveBack()?.revert(this)
}

fun EditorApp.redo() = action {
    state.history.moveForward()?.invoke(this)
}


/////////////////////////// TECHNICAL ACTIONS ///////////////////////////////

/** Runs an action, doesn't write it to history. */
internal inline fun EditorApp.action(
    crossinline execute: EditorAppImpl.() -> Unit
) {
    try {
        (this as EditorAppImpl).execute()
    } catch (e: Exception) {
        logError(e)
    }
}

/** Runs an action and also writes it to history. */
internal fun EditorApp.historyAction(action: HistoryAction) = action {
    action.invoke(this)
    state.history.append(action)
}
