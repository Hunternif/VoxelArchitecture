package hunternif.voxarch.editor.actions

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.SelectMask.*
import hunternif.voxarch.editor.builder.createGeneratorByName
import hunternif.voxarch.editor.file.readProject
import hunternif.voxarch.editor.file.writeProject
import hunternif.voxarch.editor.gui.FontAwesomeIcons
import hunternif.voxarch.editor.scene.SceneNode
import hunternif.voxarch.editor.scene.SceneObject
import hunternif.voxarch.generator.IGenerator
import hunternif.voxarch.plan.*
import hunternif.voxarch.vector.Vec3
import org.joml.Vector3i
import java.nio.file.Path

// This contains all "important" actions that modify App state,
// contribute to history and can be performed via UI.
// (As an exception, actions on project files may ignore history.)
// Some of them can support keyboard shortcuts, console commands, undo/redo.

// EditorApp must be injected into all classes that call these actions.


//============================ PROJECT FILE =============================

fun EditorApp.openProjectFile(path: Path) = action {
    clearNewNodeFrame()
    unselectAll()
    state = readProject(path)
    redrawNodes()
    redrawVoxels()
    centerCamera()
}

fun EditorApp.saveProjectFile(path: Path) = action {
    writeProject(state, path)
    state.projectPath = path
}


//=============================== VOXELS ================================

fun EditorApp.importVoxFile(path: Path) = historyAction(ImportVoxFile(path))

fun EditorApp.addGenerator(node: SceneNode, generatorName: String) {
    val generator = state.createGeneratorByName(generatorName)
    generator?.let {
        historyAction(SetGenerators(
            node, node.generators + it,
        "Add generator", FontAwesomeIcons.Landmark
        ))
    }
}

fun EditorApp.removeGenerator(node: SceneNode, generator: IGenerator) {
    val newGens = node.generators.toMutableList()
    if (newGens.remove(generator)) {
        historyAction(SetGenerators(
                node, newGens,
            "Remove generator", FontAwesomeIcons.TrashAlt
        ))
    }
}

/** Clear generated nodes and run generators for all nodes that have them. */
fun EditorApp.generateNodes() = historyAction(GenerateNodes())

/** Clear generated voxels and run a Builder on the current root node. */
fun EditorApp.buildVoxels() = historyAction(BuildVoxels())


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


//=========================== CREATE & DELETE ===========================

/**
 * Add child room to the currently active node.
 * [start] and [end] are in global 'centric' coordinates!
 */
fun EditorApp.createRoom(
    start: Vector3i, end: Vector3i, centered: Boolean = false
) : SceneNode {
    val action = CreateRoom(start, end, centered)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteSelectedObjects() = action {
    deleteObjects(state.selectedObjects)
}

fun EditorApp.deleteObjects(objs: Collection<SceneObject>) {
    if (objs.isNotEmpty()) historyAction(DeleteObjects(objs))
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
internal inline fun <T> EditorApp.action(
    crossinline execute: EditorAppImpl.() -> T
): T {
    return (this as EditorAppImpl).execute()
}

/** Runs an action and also writes it to history. */
internal fun EditorApp.historyAction(action: HistoryAction) = action {
    action.invoke(this)
    state.history.append(action)
}
