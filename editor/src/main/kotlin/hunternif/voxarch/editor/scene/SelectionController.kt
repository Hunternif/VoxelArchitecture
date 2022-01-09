package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.render.SelectionFrame
import hunternif.voxarch.editor.util.fromFloorToVoxCoords
import org.joml.AABBf
import org.lwjgl.glfw.GLFW.*

class SelectionController(
    private val camera: OrbitalCamera,
    private val editArea: AABBf,
) : MouseListener {
    var selection: SelectionFrame? = null
        private set

    val mesh = SelectionFrameMesh()

    private var dragging = false

    private var mouseX = 0f
    private var mouseY = 0f

    fun init() {
        mesh.init()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (dragging) drag(posX, posY)
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS && camera.vp.contains(mouseX, mouseY)) dragBegin()
        else if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) dragEnd()
        else dragEnd()
    }

    private fun dragBegin() {
        dragging = true
        if (selection == null) {
            val posOnFloor = camera.projectToFloor(mouseX, mouseY)
            if (editArea.testPoint(posOnFloor)) {
                val voxPos = posOnFloor.fromFloorToVoxCoords()
                selection = SelectionFrame(voxPos, voxPos)
                mesh.setSelection(selection)
            }
        }
    }

    private fun drag(posX: Double, posY: Double) {
        selection?.let {
            val posOnFloor = camera.projectToFloor(posX.toFloat(), posY.toFloat())
            it.end = posOnFloor.fromFloorToVoxCoords()
            mesh.updateEdges()
        }
    }

    private fun dragEnd() {
        dragging = false
        selection?.let {
            mesh.setSelection(null)
            selection = null
        }
    }
}