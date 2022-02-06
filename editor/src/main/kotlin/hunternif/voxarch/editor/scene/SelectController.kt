package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.actions.selectObject
import hunternif.voxarch.editor.actions.setSelectedObject
import hunternif.voxarch.editor.actions.unselectObject
import hunternif.voxarch.editor.render.OrbitalCamera
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
    private var shift = false

    // Update timer
    private var lastUpdateTime: Double = glfwGetTime()
    private val updateIntervalSeconds: Double = 0.1

    override fun onMouseDown(mods: Int) {
        dragging = true
        marqueeModel.run {
            visible = true
            start.set(mouseX - camera.vp.x, mouseY - camera.vp.y)
            end.set(start)
            update()
        }
        updateAABBs()
        if (mods and GLFW_MOD_SHIFT != 0) {
            shift = true
            origSelectedSet.clear()
            origSelectedSet.addAll(app.state.selectedObjects)
        }
        selectedSet.clear()
    }

    override fun onMouseUp(mods: Int) {
        marqueeModel.visible = false
        if (
            (selectedSet.isEmpty() || marqueeModel.end == marqueeModel.start)
            && dragging
        ) {
            // if no node was selected, pick the single node that we clicked on
            val hitNode = hitTest()
            if (shift) {
                hitNode?.let {
                    if (it in origSelectedSet) app.unselectObject(it)
                    else app.selectObject(it)
                }
            } else {
                app.setSelectedObject(hitNode)
            }
        }
        if (DEBUG_SELECT) pointsDebugModel.points.clear()
        if (DEBUG_SELECT) pointsDebugModel.update()
        shift = false
        dragging = false
    }

    override fun drag(posX: Float, posY: Float) {
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
                if (!shift || obj !in origSelectedSet)
                    app.unselectObject(obj)
                continue
            }
            if (isAABBInsideMarquee(obj)) {
                selectedSet.add(obj)
                app.selectObject(obj)
                continue
            }
            hitTestLoop@ for (x in minX..maxX step marqueeTestStep) {
                for (y in minY..maxY step marqueeTestStep) {
                    debugPoint(x, y)
                    val end = Vector3f(obj.start).add(obj.size)
                    if (camera.projectToBox(camera.vp.x + x, camera.vp.y + y, obj.start, end)) {
                        selectedSet.add(obj)
                        app.selectObject(obj)
                        break@hitTestLoop
                    } else {
                        if (!shift || obj !in origSelectedSet)
                            app.unselectObject(obj)
                    }
                }
            }
        }
        if (DEBUG_SELECT) pointsDebugModel.update()
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