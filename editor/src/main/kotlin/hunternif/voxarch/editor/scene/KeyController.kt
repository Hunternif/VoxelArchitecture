package hunternif.voxarch.editor.scene

import hunternif.voxarch.editor.EditorApp
import hunternif.voxarch.editor.actions.*
import hunternif.voxarch.editor.scene.NewNodeFrame.State.*
import imgui.ImGui
import org.lwjgl.glfw.GLFW.*

/** Contains all keyboard shortcuts in the app. */
class KeyController(private val app: EditorApp) : KeyListener {
    override fun onKeyPress(key: Int, action: Int, mods: Int) {
        if (action != GLFW_PRESS) return

        val control = (mods and GLFW_MOD_CONTROL != 0)
        val shift = (mods and GLFW_MOD_SHIFT != 0)

        if (app.state.isMainWindowFocused) {
            // Shortcuts only inside main window:
            when {
                key == GLFW_KEY_DELETE -> app.deleteSelectedObjects()
                key == GLFW_KEY_ESCAPE -> app.clearNewNodeFrame()
                key == GLFW_KEY_SPACE -> app.state.newNodeFrame.run {
                    if (state != EMPTY) {
                        app.createRoom(start, end, fromCenter)
                        app.clearNewNodeFrame()
                    }
                }
                key == GLFW_KEY_A && control -> app.selectAll()
            }
        }
        // If an ImGui item is active, it may have its own shortcuts.
        if (!ImGui.isAnyItemActive()) {
            // Shortcuts anywhere in the program:
            when {
                key == GLFW_KEY_Z && control && shift -> app.redo()
                key == GLFW_KEY_Z && control -> app.undo()
                key == GLFW_KEY_I && control -> app.openDialogImportVoxFile()
            }
        }
    }
}