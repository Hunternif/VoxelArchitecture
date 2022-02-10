package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.SelectController.Mode.*
import hunternif.voxarch.editor.scene.models.Points2DModel
import hunternif.voxarch.editor.scene.models.SelectionMarqueeModel
import org.joml.Vector2f
import org.joml.Vector3f
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
    val pointsDebugModel = Points2DModel()

    /** Whether to display debug dots */
    private val DEBUG_SELECT = false

    /** Optimization: size of step in pixels when testing whether an object
     * falls within the marquee rectangle. */
    private val marqueeTestStep = 8

    // corners of the marquee, relative to viewport
    private var minX = 0
    private var minY = 0
    private var maxX = 0
    private var maxY = 0

    /** This contains the objects currently intersecting with the marquee.
     * It's updated every frame when drawing the marquee. */
    private val selectedSet = LinkedHashSet<SceneObject>()
    /** Stores a copy of the original selection when shift-selecting. */
    private val origSelectedSet = LinkedHashSet<SceneObject>()
    private var mode = REPLACE
    /** Builder for the action that will be written to history. */
    private var selectionBuilder: SelectObjectsBuilder? = null
    /** True on first mouse-down, and false after the first move. */
    private var firstMove = false

    // Update timer
    private var lastUpdateTime: Double = glfwGetTime()
    private val updateIntervalSeconds: Double = 0.1

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
        if (DEBUG_SELECT) pointsDebugModel.points.clear()
        if (DEBUG_SELECT) pointsDebugModel.update()
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

        val currentTime = glfwGetTime()
        if (currentTime - lastUpdateTime < updateIntervalSeconds) return
        lastUpdateTime = currentTime

        minX = min(marqueeModel.start.x, marqueeModel.end.x).toInt()
        minY = min(marqueeModel.start.y, marqueeModel.end.y).toInt()
        maxX = max(marqueeModel.start.x, marqueeModel.end.x).toInt()
        maxY = max(marqueeModel.start.y, marqueeModel.end.y).toInt()

        selectedSet.clear()
        if (DEBUG_SELECT) pointsDebugModel.points.clear()

        for (obj in app.state.sceneObjects) {
            if (obj in selectedSet || obj in app.state.hiddenObjects) continue
            obj.screenAABB.run {
                debugPoint(minX, minY)
                debugPoint(maxX, minY)
                debugPoint(minX, maxY)
                debugPoint(maxX, maxY)
            }
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
                    val end = Vector3f(obj.start).add(obj.size)
                    if (camera.projectToBox(camera.vp.x + x, camera.vp.y + y, obj.start, end)) {
                        onHitObject(obj)
                        break@hitTestLoop
                    } else {
                        onMissObject(obj)
                    }
                }
            }
        }
        if (DEBUG_SELECT) pointsDebugModel.update()
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

    private fun debugPoint(x: Float, y: Float) {
        if (DEBUG_SELECT) pointsDebugModel.points.add(Vector2f(x, y))
    }
    private fun debugPoint(x: Int, y: Int) {
        if (DEBUG_SELECT) pointsDebugModel.points.add(Vector2f(x.toFloat(), y.toFloat()))
    }

    /** Returns true if the screen AABB is completely outside the marquee. */
    private fun isAABBOutsideMarquee(obj: SceneObject) =
        obj.screenAABB.maxX < minX ||
        obj.screenAABB.maxY < minY ||
        obj.screenAABB.minX > maxX ||
        obj.screenAABB.minY > maxY

    /** Returns true if the screen AABB is entirely contained inside the marquee. */
    private fun isAABBInsideMarquee(obj: SceneObject) =
        obj.screenAABB.minX >= minX &&
        obj.screenAABB.minY >= minY &&
        obj.screenAABB.maxX <= maxX &&
        obj.screenAABB.maxY <= maxY

    /** Updates AABBs of all objects in the scene. */
    private fun updateAABBs() {
        app.state.sceneObjects.forEach {
            it.screenAABB.run {
                setMin(camera.projectToViewport(it.start))
                setMax(camera.projectToViewport(it.end))
                correctBounds()
                union(camera.projectToViewport(it.start.x, it.start.y, it.end.z))
                union(camera.projectToViewport(it.start.x, it.end.y, it.start.z))
                union(camera.projectToViewport(it.start.x, it.end.y, it.end.z))
                union(camera.projectToViewport(it.end.x, it.start.y, it.start.z))
                union(camera.projectToViewport(it.end.x, it.start.y, it.end.z))
                union(camera.projectToViewport(it.end.x, it.end.y, it.start.z))
            }
        }
    }
}