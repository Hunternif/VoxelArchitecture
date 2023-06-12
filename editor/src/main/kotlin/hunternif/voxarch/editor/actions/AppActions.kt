package hunternif.voxarch.editor.actions

import hunternif.voxarch.builder.Builder
import hunternif.voxarch.dom.style.Stylesheet
import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.EditorAppImpl
import hunternif.voxarch.editor.actions.file.*
import hunternif.voxarch.editor.actions.history.HistoryAction
import hunternif.voxarch.editor.actions.history.StackingAction
import hunternif.voxarch.editor.actions.scene.*
import hunternif.voxarch.editor.actions.select.SelectMask
import hunternif.voxarch.editor.actions.select.SelectMask.ALL
import hunternif.voxarch.editor.actions.select.SelectObjectsBuilder
import hunternif.voxarch.editor.actions.style.ResetStylesheet
import hunternif.voxarch.editor.actions.style.SetStylesheet
import hunternif.voxarch.editor.actions.transform.*
import hunternif.voxarch.editor.file.writeProject
import hunternif.voxarch.editor.scenegraph.SceneNode
import hunternif.voxarch.editor.scenegraph.SceneObject
import hunternif.voxarch.editor.util.ColorRGBa
import hunternif.voxarch.plan.ClipMask
import hunternif.voxarch.plan.naturalToCentric
import hunternif.voxarch.util.SnapOrigin
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

fun EditorApp.exportVoxFile(path: Path) = action(ExportVoxFile(path))

fun EditorApp.importProject(
    path: Path,
    importBlueprints: Boolean = false,
) = historyAction(ImportProject(path, importBlueprints))


//=============================== VOXELS ================================

fun EditorApp.importVoxFile(path: Path) = historyAction(ImportVoxFile(path))

/** Clear generated nodes and run Blueprints for all nodes that have them. */
fun EditorApp.generateNodes() {
    if (state.forgetBuildHistory) action(GenerateNodes())
    else historyAction(GenerateNodes())
}

/** Clear generated voxels and run a Builder on the current root node. */
fun EditorApp.buildVoxels() {
    if (state.forgetBuildHistory) action(BuildVoxels())
    else historyAction(BuildVoxels())
}

/** Clear generated voxels and run a Builder a single node.
 * Updates the current "Built voxels" group, if it exists. */
fun EditorApp.buildOneNodeVoxels(node: SceneNode) {
    if (state.forgetBuildHistory) action(BuildNodeVoxels(node))
    else historyAction(BuildNodeVoxels(node))
}

/** Combines [generateNodes] and [buildVoxels] */
fun EditorApp.buildNodesAndVoxels() {
    if (state.forgetBuildHistory) action(BuildNodesAndVoxels())
    else historyAction(BuildNodesAndVoxels())
}

fun EditorApp.setSeed(seed: Long) = historyAction(SetSeed(seed))


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

fun EditorApp.transformNodeClipMask(
    obj: SceneNode,
    clipMask: ClipMask,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData()),
        mapOf(obj to obj.transformData(clipMask = clipMask)),
        "Transform node (clip mask)",
    )
)

/** Passing in [oldColor] because the actual color could be already changed in the scene. */
fun EditorApp.transformNodeColor(
    obj: SceneNode,
    oldColor: ColorRGBa,
    newColor: ColorRGBa,
) = historyAction(
    TransformObjects(
        mapOf(obj to obj.transformData(color = oldColor)),
        mapOf(obj to obj.transformData(color = newColor)),
        "Transform node (color)",
    )
)

/** Set tags */
fun EditorApp.setNodeTags(
    obj: SceneNode,
    newTags: Collection<String>,
) = historyAction(SetNodeTags(obj, newTags))

fun EditorApp.setNodeBuilder(
    obj: SceneNode,
    builder: Builder<*>?,
) = historyAction(SetNodeBuilder(obj, builder))


//=========================== CREATE & DELETE ===========================

/**
 * Add child node to the currently active node.
 * [start] and [end] are in global 'centric' coordinates!
 */
fun EditorApp.createNode(
    start: Vector3i, end: Vector3i,
    centered: Boolean = false,
    type: String = state.newNodeType,
): SceneNode {
    val action = CreateNode(start, end, centered, type)
    historyAction(action)
    return action.node
}

fun EditorApp.deleteSelectedObjects() {
    deleteObjects(state.selectedObjects)
}

fun EditorApp.deleteObjects(objs: Collection<SceneObject>) {
    objs.filter { it != state.rootNode && it != state.voxelRoot }.let {
        if (it.isNotEmpty()) historyAction(DeleteObjects(it))
    }
}


//=============================== OPTIONS ===============================

fun EditorApp.toggleCleanDummies() = action {
    state.cleanDummies = !state.cleanDummies
}

fun EditorApp.toggleHinting() = action {
    state.hinting = !state.hinting
}

fun EditorApp.toggleVerboseDom() = action {
    state.verboseDom = !state.verboseDom
}

fun EditorApp.toggleVerboseBuild() = action {
    state.verboseBuild = !state.verboseBuild
}

fun EditorApp.toggleForgetBuildHistory() = action {
    state.forgetBuildHistory = !state.forgetBuildHistory
}


//=============================== STYLES ================================

fun EditorApp.resetStylesheet() = historyAction(ResetStylesheet())

fun EditorApp.setStylesheet(stylesheet: Stylesheet) =
    updateStylesheetAndText(stylesheet, stylesheet.toString())

/**
 * If last action was also to update style text, then the 2 actions are
 * combined into 1.
 */
fun EditorApp.updateStylesheetAndText(stylesheet: Stylesheet, text: String) =
    historyAction(SetStylesheet(
        state.stylesheet, state.stylesheetText,
        stylesheet, text,
    ))

/** Replaces text in Style Editor with the current stylesheet text */
fun EditorApp.reloadStyleEditor() = action {
    gui.styleEditor.loadText(state.stylesheetText)
}


//=============================== HISTORY ===============================

fun EditorApp.undo() = action {
    state.history.moveBack()?.revert(this)
}

fun EditorApp.redo() = action {
    state.history.moveForward()?.invoke(this, false)
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
        if (!state.catchExceptions) throw e
    }
}

/** Runs an action, doesn't write it to history. */
internal fun EditorApp.action(
    action: AppAction,
    firstTime: Boolean = false,
) = action {
    if (action is HistoryAction) action.invoke(this, firstTime)
    else action.invoke(this)
}

/** Runs an action and also writes it to history. */
internal fun EditorApp.historyAction(action: HistoryAction) = action {
    action.invoke(this, true)
    val last = state.history.last()
    if (last != null && last is StackingAction<*> && last.stacksWith(action)) {
        last.maybeUpdate(action)
    } else {
        state.history.append(action)
    }
}
