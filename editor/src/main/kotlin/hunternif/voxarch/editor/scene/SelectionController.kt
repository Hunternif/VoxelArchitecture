package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.BoxInstancedModel.InstanceData
import hunternif.voxarch.editor.scene.models.NodeModel
import hunternif.voxarch.editor.scene.models.NodeModel.NodeData
import hunternif.voxarch.editor.scene.models.Points2DModel
import hunternif.voxarch.editor.scene.models.SelectionMarqueeModel
import hunternif.voxarch.plan.Node
import imgui.internal.ImGui
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.max
import kotlin.math.min

class SelectionController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
    private val nodeModel: NodeModel,
) : InputListener {
    val marqueeModel = SelectionMarqueeModel()
    val pointsDebugModel = Points2DModel()

    /** Whether to display debug dots */
    private val DEBUG_SELECT = false

    /** Optimization: size of step in pixels when testing whether a node falls
     * within the marquee rectangle. */
    private val marqueeTestStep = 8

    // mouse coordinates are relative to window
    private var mouseX = 0f
    private var mouseY = 0f
    private var dragging = false

    // corners of the marquee, relative to viewport
    private var minX = 0
    private var minY = 0
    private var maxX = 0
    private var maxY = 0

    /** This contains the nodes currently intersecting with the marquee.
     * It's updated every frame when drawing the marquee. */
    private val selectedNodes = LinkedHashSet<InstanceData<NodeData>>()

    // Update timer
    private var lastUpdateTime: Double = glfwGetTime()
    private val updateIntervalSeconds: Double = 0.1

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (app.currentTool != Tool.SELECT) return
        if (dragging) drag(posX, posY)
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered()) return
        if (app.currentTool == Tool.SELECT && button == GLFW_MOUSE_BUTTON_1) {
            if (action == GLFW_PRESS && camera.vp.contains(mouseX, mouseY)
            ) {
                onMouseDown()
            } else if (action == GLFW_RELEASE) {
                onMouseUp()
            }
        }
    }

    private fun onMouseDown() {
        dragging = true
        marqueeModel.run {
            visible = true
            start.set(mouseX - camera.vp.x, mouseY - camera.vp.y)
            end.set(start)
            update()
        }
        updateAABBs()
        selectedNodes.clear()
    }

    private fun onMouseUp() {
        dragging = false
        marqueeModel.visible = false
        if (selectedNodes.isEmpty() || marqueeModel.end == marqueeModel.start) {
            selectSingleNode()
        }
        if (DEBUG_SELECT) pointsDebugModel.points.clear()
        if (DEBUG_SELECT) pointsDebugModel.update()
    }

    private fun drag(posX: Double, posY: Double) {
        marqueeModel.end.set(posX.toFloat() - camera.vp.x, posY.toFloat() - camera.vp.y)
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
            inst.data.screenAABB.run {
                debugPoint(minX, minY)
                debugPoint(maxX, minY)
                debugPoint(minX, maxY)
                debugPoint(maxX, maxY)
            }
            if (isAABBOutsideMarquee(inst)) {
                app.unselectNode(inst.data.node)
                continue
            }
            if (isAABBInsideMarquee(inst)) {
                selectedNodes.add(inst)
                app.selectNode(inst.data.node)
                continue
            }
            hitTestLoop@ for (x in minX..maxX step marqueeTestStep) {
                for (y in minY..maxY step marqueeTestStep) {
                    debugPoint(x, y)
                    val end = Vector3f(inst.start).add(inst.size)
                    if (camera.projectToBox(camera.vp.x + x, camera.vp.y + y, inst.start, end)) {
                        selectedNodes.add(inst)
                        app.selectNode(inst.data.node)
                        break@hitTestLoop
                    } else {
                        app.unselectNode(inst.data.node)
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

    private fun selectSingleNode() {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitNode: Node? = null
        for (inst in nodeModel.instances) {
            val hit = camera.projectToBox(mouseX, mouseY, inst.start, inst.end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitNode = inst.data.node
            }
        }
        app.setSelectedNode(hitNode)
    }

    /** Returns true if the screen AABB is completely outside the marquee. */
    private fun isAABBOutsideMarquee(inst: InstanceData<NodeData>) =
        inst.data.screenAABB.maxX < minX ||
        inst.data.screenAABB.maxY < minY ||
        inst.data.screenAABB.minX > maxX ||
        inst.data.screenAABB.minY > maxY

    /** Returns true if the screen AABB is entirely contain inside the marquee. */
    private fun isAABBInsideMarquee(inst: InstanceData<NodeData>) =
        inst.data.screenAABB.minX >= minX &&
        inst.data.screenAABB.minY >= minY &&
        inst.data.screenAABB.maxX <= maxX &&
        inst.data.screenAABB.maxY <= maxY

    /** Updates AABBs of nodes in [nodeModel] */
    private fun updateAABBs() {
        nodeModel.instances.forEach {
            it.data.screenAABB.run {
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