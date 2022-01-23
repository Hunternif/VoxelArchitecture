package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.createRoom
import hunternif.voxarch.editor.deleteSelectedNodes
import hunternif.voxarch.editor.scene.NewNodeFrame.State.*
import org.lwjgl.glfw.GLFW.*

/** Contains all keyboard shortcuts in the app. */
class KeyController(private val app: EditorApp) : KeyListener {
    @Suppress("UNUSED_PARAMETER")
    override fun onKeyPress(key: Int, action: Int, mods: Int) {
        if (app.gui.isMainWindowFocused && action == GLFW_PRESS) {
            when (key) {
                GLFW_KEY_DELETE -> app.deleteSelectedNodes()
                GLFW_KEY_ESCAPE -> app.scene.newNodeController.setState(EMPTY)
                GLFW_KEY_SPACE -> app.scene.newNodeController.apply {
                    if (frame.state != EMPTY) {
                        setState(EMPTY)
                        app.createRoom(frame.start, frame.end, fromCenter)
                    }
                }
            }
        }
    }
}