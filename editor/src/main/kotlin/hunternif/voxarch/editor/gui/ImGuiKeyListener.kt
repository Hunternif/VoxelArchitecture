package hunternif.voxarch.editor.gui

import hunternif.voxarch.editor.scene.KeyListener
import imgui.ImGui
import org.lwjgl.glfw.GLFW.*

class ImGuiKeyListener : KeyListener {
    override fun onKeyPress(key: Int, action: Int, mods: Int) {
        val io = ImGui.getIO()
        if (key in 0..511) {
            if (action == GLFW_PRESS)
                io.setKeysDown(key, true)
            if (action == GLFW_RELEASE)
                io.setKeysDown(key, false)
        }

        io.keyCtrl = io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL)
        io.keyShift = io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT)
        io.keyAlt = io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT)
    }
}