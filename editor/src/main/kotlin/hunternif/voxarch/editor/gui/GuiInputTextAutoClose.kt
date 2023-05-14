package hunternif.voxarch.editor.gui

import imgui.ImGui
import org.lwjgl.glfw.GLFW

/**
 * Text input that appears temporarily and auto-closes.
 */
class GuiInputTextAutoClose(val label: String, hint: String? = null) {
    @PublishedApi
    internal val input = GuiInputText(label, hint)

    @PublishedApi
    internal var wasFocused = false

    inline fun render(
        initialValue: String,
        crossinline onCancel: () -> Unit = {},
        crossinline onSubmit: (newValue: String) -> Unit = {},
    ) {
        input.render(initialValue)
        if (ImGui.isItemActive()) {
            wasFocused = true
            if (ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                onCancel()
            }
        } else {
            if (wasFocused) {
                // Immediately after losing focus
                wasFocused = false
                if (ImGui.isKeyPressed(GLFW.GLFW_KEY_ENTER)
                    || ImGui.isKeyPressed(GLFW.GLFW_KEY_KP_ENTER)
                ) {
                    onSubmit(input.data.get())
                } else {
                    onCancel()
                }
            } else {
                if (ImGui.isAnyMouseDown()) {
                    onCancel()
                }
            }
        }
    }
}