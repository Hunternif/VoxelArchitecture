package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.gui.Timer
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.SelectController.Mode.*
import hunternif.voxarch.editor.scene.models.Points2DModel
import hunternif.voxarch.editor.scene.models.SelectionMarqueeModel
import hunternif.voxarch.editor.scenegraph.SceneObject
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.max
import kotlin.math.min

/** Draws a 2D rectangular marquee on screen, selecting any objects under it. */
class SelectController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : BaseSelectionController(app, camera, Tool.SELECT) {
    /** How to modify the existing selection. */
    private enum class Mode {
        REPLACE, ADD, SUBTRACT
    }

    val marqueeModel = SelectionMarqueeModel()

    /** Optimization: size of step in pixels when testing whether an object
     * falls within the marquee rectangle. */
    private val marqueeTestStep = 8
    val pointsDebugModel = Points2DModel()

    /** Whether to display debug dots */
    private val DEBUG_SELECT = false

    // corners of the marquee, relative to viewport
    private var minX = 0
    private var minY = 0
    private var maxX = 0
    private var maxY = 0

    /** This contains the objects currently intersecting with the marquee.
     * It's updated every frame as we drag the marquee. */
    private val selectedSet = LinkedHashSet<SceneObject>()
    /** Stores a copy of the original selection when shift-selecting. */
    private val origSelectedSet = LinkedHashSet<SceneObject>()
    private var mode = REPLACE
    /** Builder for the action that will be written to history. */
    private var selectionBuilder: SelectObjectsBuilder? = null
    /** True on first mouse-down, and false after the first move.
     * This is needed so that mouse-down doesn't immediately clear selection. */
    private var firstMove = false

    // Update timer
    private val updateTimer = Timer(0.1)

    override fun onMouseDown(mods: Int) {
        dragging = true
        firstMove = true
        marqueeModel.run {
            visible = true
            start.set(mouseX - camera.vp.x, mouseY - camera.vp.y)
            end.set(start)
            update()
        }
        updateAABBs()
        if (mods and GLFW_MOD_SHIFT != 0) {
            mode = ADD
            origSelectedSet.addAll(app.state.selectedObjects)
        } else if (mods and GLFW_MOD_ALT != 0) {
            mode = SUBTRACT
            origSelectedSet.addAll(app.state.selectedObjects)
        }
        if (DEBUG_SELECT) pointsDebugModel.clear()
        selectedSet.clear()
        selectionBuilder = app.selectionBuilder()
    }

    override fun onMouseUp(mods: Int) {
        marqueeModel.visible = false
        if (
            (selectedSet.isEmpty() || marqueeModel.end == marqueeModel.start)
            && dragging
        ) {
            // if no node was selected, pick the single node that we clicked on
            val hit = hitTest()
            when (mode) {
                ADD -> {
                    hit?.let {
                        // in ADD mode, single click toggles selection
                        if (it in origSelectedSet) selectionBuilder?.remove(it)
                        else selectionBuilder?.add(it)
                    }
                }
                SUBTRACT -> {
                    hit?.let { selectionBuilder?.remove(it) }
                }
                REPLACE -> {
                    selectionBuilder?.clear()
                    hit?.let { selectionBuilder?.add(it) }
                }
            }
        }
        mode = REPLACE
        dragging = false
        origSelectedSet.clear()
        selectionBuilder?.commit()
        selectionBuilder = null
    }

    override fun drag(posX: Float, posY: Float) {
        if (firstMove) {
            firstMove = false
            if (mode === REPLACE) selectionBuilder?.clear()
        }
        marqueeModel.end.set(posX - camera.vp.x, posY - camera.vp.y)
        marqueeModel.update()

        updateTimer.runAtInterval {
            minX = min(marqueeModel.start.x, marqueeModel.end.x).toInt()
            minY = min(marqueeModel.start.y, marqueeModel.end.y).toInt()
            maxX = max(marqueeModel.start.x, marqueeModel.end.x).toInt()
            maxY = max(marqueeModel.start.y, marqueeModel.end.y).toInt()

            selectedSet.clear()
            if (DEBUG_SELECT) pointsDebugModel.clear()

            for (obj in app.state.sceneObjects) {
                obj.box.screenTriangles.forEach {
                    debugPoint(it.p1)
                    debugPoint(it.p2)
                    debugPoint(it.p3)
                }
                if (obj in selectedSet || obj in app.state.hiddenObjects) continue
                if (isAABBOutsideMarquee(obj)) {
                    onMissObject(obj)
                    continue
                }
                if (isAABBInsideMarquee(obj)) {
                    onHitObject(obj)
                    continue
                }
                hitTestLoop@ for (x in minX..maxX step marqueeTestStep) {
                    for (y in minY..maxY step marqueeTestStep) {
                        debugPoint(x, y)
                        if (obj.hitTest(camera, camera.vp.x + x, camera.vp.y + y)) {
                            onHitObject(obj)
                            break@hitTestLoop
                        } else {
                            onMissObject(obj)
                        }
                    }
                }
            }
        }
    }

    /** When the selection marquee includes [obj] */
    private fun onHitObject(obj: SceneObject) {
        when (mode) {
            REPLACE, ADD -> {
                selectedSet.add(obj)
                selectionBuilder?.add(obj)
            }
            SUBTRACT -> {
                selectedSet.remove(obj)
                selectionBuilder?.remove(obj)
            }
        }
    }

    /** When the selection marquee excludes [obj] */
    private fun onMissObject(obj: SceneObject) {
        when (mode) {
            REPLACE -> selectionBuilder?.remove(obj)
            ADD -> if (obj !in origSelectedSet) selectionBuilder?.remove(obj)
            SUBTRACT -> if (obj in origSelectedSet) selectionBuilder?.add(obj)
        }
    }

    private fun debugPoint(p: Vector2f) {
        if (DEBUG_SELECT) pointsDebugModel.add(p)
    }
    private fun debugPoint(x: Number, y: Number) {
        debugPoint(Vector2f(x.toFloat(), y.toFloat()))
    }

    /** Returns true if the screen AABB is completely outside the marquee. */
    private fun isAABBOutsideMarquee(obj: SceneObject) =
        obj.box.screenAABB.maxX < minX ||
        obj.box.screenAABB.maxY < minY ||
        obj.box.screenAABB.minX > maxX ||
        obj.box.screenAABB.minY > maxY

    /** Returns true if the screen AABB is entirely contained inside the marquee. */
    private fun isAABBInsideMarquee(obj: SceneObject) =
        obj.box.screenAABB.minX >= minX &&
        obj.box.screenAABB.minY >= minY &&
        obj.box.screenAABB.maxX <= maxX &&
        obj.box.screenAABB.maxY <= maxY

    /** Updates AABBs of all objects in the scene. */
    private fun updateAABBs() {
        app.state.sceneObjects.forEach {
            it.updateScreenProjection(camera)
        }
    }
}