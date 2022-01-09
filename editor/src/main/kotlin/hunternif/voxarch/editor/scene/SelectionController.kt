package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.render.SelectionFrame
import hunternif.voxarch.editor.render.SelectionFrame.State.*
import hunternif.voxarch.editor.util.fromFloorToVoxCoords
import org.joml.AABBf
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.glfw.GLFW.*
import kotlin.math.round

class SelectionController(
    private val camera: OrbitalCamera,
    private val editArea: AABBf,
) : MouseListener {
    val selection = SelectionFrame()
    val mesh = SelectionFrameMesh(selection)

    /** Used to store the end position while choosing height */
    private var endBeforeComplete = Vector3f()

    private var mouseX = 0f
    private var mouseY = 0f

    fun init() {
        mesh.init()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        selection.run {
            when (state) {
                EMPTY -> {}
                CHOOSING_BASE -> {
                    val posOnFloor = camera.projectToFloor(posX.toFloat(), posY.toFloat())
                    end = posOnFloor.fromFloorToVoxCoords()
                    mesh.updateEdges()
                }
                CHOOSING_HEIGHT -> {
                    val posOnWall = camera.projectToVertical(
                        posX.toFloat(), posY.toFloat(), endBeforeComplete
                    )
                    end.y = round(posOnWall.y).toInt()
                    correctBounds()
                    mesh.updateEdges()
                }
                COMPLETE -> {}
            }
        }
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS && camera.vp.contains(mouseX, mouseY)) onMouseDown()
        else if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) onMouseUp()
        // cancel current selection operation if any other mouse button is pressed:
        else cancelOperation()
    }

    private fun onMouseDown() {
        selection.run {
            when (state) {
                EMPTY -> {
                    val posOnFloor = camera.projectToFloor(mouseX, mouseY)
                    if (editArea.testPoint(posOnFloor)) {
                        val voxPos = posOnFloor.fromFloorToVoxCoords()
                        selection.start = Vector3i(voxPos)
                        selection.end = Vector3i(voxPos)
                        setState(CHOOSING_BASE)
                    }
                }
                CHOOSING_BASE -> setState(CHOOSING_HEIGHT) // This shouldn't happen!
                CHOOSING_HEIGHT -> setState(COMPLETE)
                COMPLETE -> setState(EMPTY)
            }
        }
    }

    private fun onMouseUp() {
        selection.run {
            when (state) {
                EMPTY -> {}
                CHOOSING_BASE -> {
                    endBeforeComplete.set(end)
                    setState(CHOOSING_HEIGHT)
                }
                CHOOSING_HEIGHT -> setState(COMPLETE)
                COMPLETE -> {}
            }
        }
    }

    private fun setState(state: SelectionFrame.State) {
        selection.state = state
        mesh.updateEdges()
    }

    private fun cancelOperation() {
        selection.run {
            when (state) {
                EMPTY -> {}
                CHOOSING_BASE -> setState(EMPTY)
                CHOOSING_HEIGHT -> setState(COMPLETE)
                COMPLETE -> {}
            }
        }
    }
}