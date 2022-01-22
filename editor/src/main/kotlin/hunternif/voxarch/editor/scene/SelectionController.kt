package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.NodeModel
import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.editor.scene.models.Points2DModel
import hunternif.voxarch.editor.scene.models.SelectionMarqueeModel
import hunternif.voxarch.plan.Node
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.max
import kotlin.math.min

class SelectionController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
    private val nodeModel: NodeModel,
) : BaseSelectionController(app, camera, nodeModel, Tool.SELECT) {
    val marqueeModel = SelectionMarqueeModel()
    val pointsDebugModel = Points2DModel()

    /** Whether to display debug dots */
    private val DEBUG_SELECT = false

    /** Optimization: size of step in pixels when testing whether a node falls
     * within the marquee rectangle. */
    private val marqueeTestStep = 8

    // corners of the marquee, relative to viewport
    private var minX = 0
    private var minY = 0
    private var maxX = 0
    private var maxY = 0

    /** This contains the nodes currently intersecting with the marquee.
     * It's updated every frame when drawing the marquee. */
    private val selectedNodes = LinkedHashSet<NodeData>()
    /** Stores a copy of the original selection when shift-selecting. */
    private val origSelectedNodes = LinkedHashSet<Node>()
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
            origSelectedNodes.clear()
            origSelectedNodes.addAll(app.selectedNodes)
        }
        selectedNodes.clear()
    }

    override fun onMouseUp(mods: Int) {
        dragging = false
        marqueeModel.visible = false
        if (selectedNodes.isEmpty() || marqueeModel.end == marqueeModel.start) {
            // if no node was selected, pick the single node that we clicked on
            val hitNode = hitTestNode()
            if (shift) {
                hitNode?.let {
                    if (it in origSelectedNodes) app.unselectNode(it)
                    else app.selectNode(it)
                }
            } else {
                app.setSelectedNode(hitNode)
            }
        }
        if (DEBUG_SELECT) pointsDebugModel.points.clear()
        if (DEBUG_SELECT) pointsDebugModel.update()
        shift = false
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

        selectedNodes.clear()
        if (DEBUG_SELECT) pointsDebugModel.points.clear()

        for (inst in nodeModel.instances) {
            if (selectedNodes.contains(inst)) continue
            inst.screenAABB.run {
                debugPoint(minX, minY)
                debugPoint(maxX, minY)
                debugPoint(minX, maxY)
                debugPoint(maxX, maxY)
            }
            if (isAABBOutsideMarquee(inst)) {
                if (!shift || inst.node !in origSelectedNodes)
                    app.unselectNode(inst.node)
                continue
            }
            if (isAABBInsideMarquee(inst)) {
                selectedNodes.add(inst)
                app.selectNode(inst.node)
                continue
            }
            hitTestLoop@ for (x in minX..maxX step marqueeTestStep) {
                for (y in minY..maxY step marqueeTestStep) {
                    debugPoint(x, y)
                    val end = Vector3f(inst.start).add(inst.size)
                    if (camera.projectToBox(camera.vp.x + x, camera.vp.y + y, inst.start, end)) {
                        selectedNodes.add(inst)
                        app.selectNode(inst.node)
                        break@hitTestLoop
                    } else {
                        if (!shift || inst.node !in origSelectedNodes)
                            app.unselectNode(inst.node)
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
    private fun isAABBOutsideMarquee(inst: NodeData) =
        inst.screenAABB.maxX < minX ||
        inst.screenAABB.maxY < minY ||
        inst.screenAABB.minX > maxX ||
        inst.screenAABB.minY > maxY

    /** Returns true if the screen AABB is entirely contained inside the marquee. */
    private fun isAABBInsideMarquee(inst: NodeData) =
        inst.screenAABB.minX >= minX &&
        inst.screenAABB.minY >= minY &&
        inst.screenAABB.maxX <= maxX &&
        inst.screenAABB.maxY <= maxY

    /** Updates AABBs of nodes in [nodeModel] */
    private fun updateAABBs() {
        nodeModel.instances.forEach {
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

    @Suppress("UNUSED_PARAMETER")
    override fun onKeyPress(key: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            when (key) {
                GLFW_KEY_DELETE -> {
                    app.deleteSelectedNodes()
                }
            }
        }
    }
}