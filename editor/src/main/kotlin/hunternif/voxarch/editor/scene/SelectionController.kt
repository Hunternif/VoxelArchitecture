package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.BoxInstancedModel.InstanceData
import hunternif.voxarch.editor.scene.models.NodeModel
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
    val model = SelectionMarqueeModel()
    val pointsDebugModel = Points2DModel()

    private val DEBUG_SELECT = true

    /** Optimization: size of step in pixels when testing whether a node falls
     * within the marquee rectangle. */
    private val marqueeTestStep = 4

    private var mouseX = 0f
    private var mouseY = 0f
    private var dragging = false

    // temp variables
    private val selectedNodes = LinkedHashSet<InstanceData<Node>>()

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
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
        model.run {
            visible = true
            start.set(mouseX, mouseY)
            end.set(mouseX, mouseY)
            update()
        }
        selectedNodes.clear()
    }

    private fun onMouseUp() {
        dragging = false
        model.visible = false
        if (selectedNodes.isEmpty() || model.end == model.start) {
            selectSingleNode()
        }
        if (DEBUG_SELECT) pointsDebugModel.points.clear()
        if (DEBUG_SELECT) pointsDebugModel.update()
    }

    private fun drag(posX: Double, posY: Double) {
        model.end.set(posX.toFloat(), posY.toFloat())
        model.update()
        val minX = min(model.start.x, model.end.x).toInt()
        val minY = min(model.start.y, model.end.y).toInt()
        val maxX = max(model.start.x, model.end.x).toInt()
        val maxY = max(model.start.y, model.end.y).toInt()

        //TODO: throttle or optimize space partition if necessary

        selectedNodes.clear()
        if (DEBUG_SELECT) pointsDebugModel.points.clear()

        for (x in minX..maxX step marqueeTestStep) {
            for (y in minY..maxY step marqueeTestStep) {
                if (DEBUG_SELECT) pointsDebugModel.points.add(Vector2f(x.toFloat(), y.toFloat()))
                for (inst in nodeModel.instances) {
                    if (selectedNodes.contains(inst)) continue
                    val end = Vector3f(inst.start).add(inst.size)
                    if (camera.projectToBox(x.toFloat(), y.toFloat(), inst.start, end)) {
                        selectedNodes.add(inst)
                        app.selectNode(inst.data)
                    } else {
                        app.unselectNode(inst.data)
                    }
                }
            }
        }
        if (DEBUG_SELECT) pointsDebugModel.update()
    }

    private fun selectSingleNode() {
        val result = Vector2f()
        var minDistance = Float.MAX_VALUE
        var hitNode: Node? = null
        for (inst in nodeModel.instances) {
            val end = Vector3f(inst.start).add(inst.size)
            val hit = camera.projectToBox(mouseX, mouseY, inst.start, end, result)
            if (hit && result.x < minDistance) {
                minDistance = result.x
                hitNode = inst.data
            }
        }
        app.setSelectedNode(hitNode)
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