package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.Tool
import hunternif.voxarch.editor.actions.addOverlayText
import hunternif.voxarch.editor.actions.removeOverlayText
import hunternif.voxarch.editor.render.OrbitalCamera
import hunternif.voxarch.editor.scene.NewNodeFrame.State.*
import hunternif.voxarch.editor.scene.models.NewNodeFrameModel
import hunternif.voxarch.editor.util.fromFloorToVoxCoords
import imgui.internal.ImGui
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.glfw.GLFW.*
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.round

class NewNodeController(
    private val app: EditorApp,
    private val camera: OrbitalCamera,
) : MouseListener {
    private val overlayIdStart: String = "new_node_start"
    private val overlayIdSize: String = "new_node_size"
    private val vecFormat = DecimalFormat()

    private val frame: NewNodeFrame get() = app.state.newNodeFrame
    val model = NewNodeFrameModel()

    private val origStart = Vector3i()
    private val newEnd = Vector3i()

    /** Used to store the end position while choosing height */
    private var endBeforeComplete = Vector3f()

    private var mouseX = 0f
    private var mouseY = 0f

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseMove(posX: Double, posY: Double) {
        if (app.state.currentTool != Tool.ADD_NODE) return
        frame.run {
            when (state) {
                EMPTY -> {}
                CHOOSING_BASE -> {
                    val posOnFloor = camera.projectToFloor(posX.toFloat(), posY.toFloat())
                    newEnd.set(posOnFloor.fromFloorToVoxCoords())
                    if (fromCenter) {
                        val halfSize = Vector3i(newEnd).sub(origStart)
                        start.set(origStart).sub(halfSize)
                    }
                    end.set(newEnd)
                    correctBounds()
                    model.updateEdges(frame)
                    app.addOverlayText(overlayIdSize, "size: ${size.toString(vecFormat)}")
                }
                CHOOSING_HEIGHT -> {
                    val posOnWall = camera.projectToY(
                        posX.toFloat(), posY.toFloat(), endBeforeComplete
                    )
                    // must have Y >= 0
                    end.y = round(max(0f, posOnWall.y)).toInt()
                    correctBounds()
                    model.updateEdges(frame)
                    app.addOverlayText(overlayIdSize, "size: ${size.toString(vecFormat)}")
                }
                COMPLETE -> {}
            }
        }
        mouseX = posX.toFloat()
        mouseY = posY.toFloat()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun onMouseButton(button: Int, action: Int, mods: Int) {
        if (ImGui.isAnyItemHovered() ||
            app.state.currentTool != Tool.ADD_NODE ||
            !camera.vp.contains(mouseX, mouseY)
        ) {
            return
        }
        if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
            onMouseDown(mods)
        } else if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
            onMouseUp()
        } else {
            // cancel current selection operation if any other mouse button is pressed:
            cancelOperation()
        }
    }

    private fun onMouseDown(mods: Int) {
        if (frame.state == EMPTY) {
            frame.fromCenter = mods and GLFW_MOD_ALT != 0
        }
        frame.run {
            when (state) {
                EMPTY -> {
                    val posOnFloor = camera.projectToFloor(mouseX, mouseY)
                    origStart.set(posOnFloor.fromFloorToVoxCoords())
                    frame.start = Vector3i(origStart)
                    frame.end = Vector3i(origStart)
                    setState(CHOOSING_BASE)
                    app.addOverlayText(overlayIdStart, "start: ${origStart.toString(vecFormat)}")
                }
                CHOOSING_BASE -> setState(CHOOSING_HEIGHT) // This shouldn't happen!
                CHOOSING_HEIGHT -> setState(COMPLETE)
                COMPLETE -> setState(EMPTY)
            }
        }
    }

    private fun onMouseUp() {
        frame.run {
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

    fun setState(state: NewNodeFrame.State) {
        frame.state = state
        model.updateEdges(frame)
        if (state == EMPTY) {
            app.removeOverlayText(overlayIdStart)
            app.removeOverlayText(overlayIdSize)
        }
    }

    private fun cancelOperation() {
        frame.run {
            when (state) {
                EMPTY -> {}
                CHOOSING_BASE -> setState(EMPTY)
                CHOOSING_HEIGHT -> setState(COMPLETE)
                COMPLETE -> {}
            }
        }
    }
}