package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.*
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.models.NodeModel
import hunternif.voxarch.plan.Node
import imgui.internal.ImGui
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*

class SelectionController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
    private val nodeModel: NodeModel,
) : InputListener {

    private var mouseX = 0f
    private var mouseY = 0f

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered()) return
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS &&
            camera.vp.contains(mouseX, mouseY) && app.currentTool == Tool.SELECT
        ) {
            onMouseDown()
        }
    }

    private fun onMouseDown() {
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